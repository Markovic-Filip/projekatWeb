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
        valid: true
    },
    mounted()   {
        this.apartman = JSON.parse(window.localStorage.getItem('apartman'));
        if (this.apartman == null)   {
            window.location = 'http://localhost:8080/apartmani.html';
        }/* else  {
            window.localStorage.removeItem('apartman'); // Premestio sam ovo posle ajax poziva za slanje rezervacije
        }*/

        let datumi = [];
        // TODO: otkomentarisati kad se uradi back end
        //for (datum of this.apartman.zauzetiDatumi)  {
          //  datumi.push(new Date(datum));
        //}

        this.state = {
            disabledDates: {
                to: new Date(),
                dates: datumi
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
        
        capitalize: function(string)    {
            return string.charAt(0).toUpperCase() + string.slice(1);
        }
    }
});
