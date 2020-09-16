new Vue({
    el: '#rezervacija-app',
    data:   {
        apartman: {},
        sadrzaji: [],
        komentari: [],
        state: {},
        pocetniDatum: new Date(parseInt(Date.now() + 86400000)),
        brojNocenja: 1,
        poruka: '',
        valid: true,
        datumi: []
    },
    mounted()   {
        this.apartman = JSON.parse(window.localStorage.getItem('apartman'));
        if (this.apartman == null)   {
            window.location = 'apartmani.html';
        }/* else  {
            window.localStorage.removeItem('apartman'); // Premestio sam ovo posle ajax poziva za slanje rezervacije
        }*/

        for (datum of this.apartman.zauzetiDatumi)  {
            this.datumi.push(new Date(datum));
        }

        this.state = {
            disabledDates: {
                to: new Date(),
                dates: this.datumi
            }
        };

        axios
            .get('app/dobavi_sadrzaj_apartmana', {
                params: {
                    idSadrzaja: JSON.stringify(this.apartman.idSadrzaja)
                }
            })
            .then(response => {
                this.sadrzaji = response.data;
            })
            .catch(error => {
                console.log(error);
                alert(error.response.data.sadrzaj);
            });
        
        axios
            .get('app/dobavi_komentare', {
                params: {
                    idApartmana: this.apartman.id
                }
            })
            .then(response => {
                this.komentari = response.data;
            })
            .catch(error => {
                console.log(error);
                alert(error.response.data.sadrzaj);
            });
    },
    components: {
        vuejsDatepicker
    },
    methods:    {
        ulogovanKorisnik: function()    {
            return window.localStorage.getItem('jwt') != null;
        },

        validacija: function()  {
            this.valid = true;

            this.pocetniDatum.setHours(this.apartman.vremeZaPrijavu);

            if (this.brojNocenja < 1)    {
                this.$refs.broj_nocenja.classList.remove("dobra-vrednost");
                this.$refs.broj_nocenja.classList.add("losa-vrednost");
                this.valid = false;
            } else  {
                if (this.$refs.broj_nocenja.classList.contains("losa-vrednost"))   {
                    this.$refs.broj_nocenja.classList.remove("losa-vrednost");
                    this.$refs.broj_nocenja.classList.add("dobra-vrednost");
                }
            }

            if (this.pocetniDatum.getTime() < Date.now())   {
                alert('Izabran početni datum nije validan.');
                this.valid = false;
            }

            if (this.poruka === '') {
                this.$refs.poruka.classList.remove("dobra-vrednost");
                this.$refs.poruka.classList.add("losa-vrednost");
                this.valid = false;
            } else  {
                if (this.$refs.poruka.classList.contains("losa-vrednost"))   {
                    this.$refs.poruka.classList.remove("losa-vrednost");
                    this.$refs.poruka.classList.add("dobra-vrednost");
                }
            }

            for(let i=0;i<this.brojNocenja;i++){
            	let tekuciDatum = new Date(this.pocetniDatum.getTime()+i*86400000);
            	for(dat of this.datumi){
            		if(dat===tekuciDatum){
            			this.valid = false;
            		}
            	}
            }

            if (this.valid)  {
                this.rezervisi();
            }
        },

        rezervisi: function()   {
            let rezervacija = {
                'apartmanId': this.apartman.id,
                'pocetniDatum': this.pocetniDatum.getTime(),
                'brojNocenja': this.brojNocenja,
                'cena': this.apartman.cenaPoNoci * this.brojNocenja,
                'poruka': this.poruka
            }

            axios
                .post('app/napravi_rezervaciju', rezervacija, {
                    headers: {
                        'Content-Type': 'application/json',
                        'Authorization': 'Bearer ' + window.localStorage.getItem('jwt')
                    }
                })
                .then(response => {
                    //this.$refs.msg.classList.add("ok-msg");
                    this.$refs.msg.classList.remove("error-msg");
                    this.$refs.msg.innerHTML = response.data.sadrzaj;
                    window.localStorage.removeItem('apartman'); // Ne treba vise
                })
                .catch(error => {
                    console.log(error);
                    //this.$refs.msg.classList.remove("ok-msg");
                    this.$refs.msg.classList.add("error-msg");
                    this.$refs.msg.innerHTML = error.response.data.sadrzaj;
                    if (error.response.status == 400 || error.response.status == 403)   {
                        window.localStorage.removeItem('apartman');
                        window.localStorage.removeItem('jwt');
                        window.location = 'login.html';
                    }
                });
        },

        prikaziAdresu: function()   {
            return this.apartman.lokacija.adresa['ulica'] + " " + this.apartman.lokacija.adresa['broj'] + ", " + this.apartman.lokacija.adresa['mesto'] + ", " + this.apartman.lokacija.adresa['postanskiBroj'];
        },
        
        prikaziSliku: function(slika)    {
            //return atob(slika);
            return "data:image/jpeg;base64," + slika;
        },

        /*otvoriSliku: function (slika)    {
            var newTab = window.open();
            newTab.document.body.innerHTML = '<img src="data:image/jpeg;base64,' + slika + '" width="600px" height="400px">';
        },*/

        otvoriSliku: function (slika) {
            var image = new Image();
            image.src = "data:image/jpg;base64," + slika;
    
            var w = window.open("");
            w.document.write(image.outerHTML);
            w.document.close();
        },

        capitalize: function(string)    {
            return string.charAt(0).toUpperCase() + string.slice(1);
        }
    }
});
