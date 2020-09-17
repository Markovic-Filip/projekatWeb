new Vue({
    el: '#domacin-app',
    data:   {
        aktivni_apartmani: [],
        neaktivni_apartmani: [],
        sadrzaji: [],
        sortiranje: undefined,
        pretraga_tip: '2',
        pretraga_sadrzaji: [],

        rezervacije: [],
        pretraga_rezervacije: '',
        sortiranje_rezervacije: undefined,
        pretraga_status: '5'
    },
    mounted()  {
        axios
            .get('app/dobavi_aktivne_apartmane', {
                headers: {
                    'Authorization': 'Bearer ' + window.localStorage.getItem('jwt')
                }
            })
            .then(response => {
                this.aktivni_apartmani = response.data;
            })
            .catch(error => {
                console.log(error);
                alert(error.response.data.sadrzaj);
                if (error.response.status == 400 || error.response.status == 403)   {
                    window.localStorage.removeItem('jwt');
                    window.location = 'login.html';
                }
            });
        
        axios
            .get('app/dobavi_neaktivne_apartmane', {
                headers: {
                    'Authorization': 'Bearer ' + window.localStorage.getItem('jwt')
                }
            })
            .then(response => {
                this.neaktivni_apartmani = response.data;
            })
            .catch(error => {
                console.log(error);
                alert(error.response.data.sadrzaj);
                if (error.response.status == 400 || error.response.status == 403)   {
                    window.localStorage.removeItem('jwt');
                    window.location = 'login.html';
                }
            });

        axios
            .get('app/dobavi_sadrzaj_apartmana')
            .then(response => {
                this.sadrzaji = response.data;
            })
            .catch(error => {
                alert(error.response.data.sadrzaj);
            });

        axios
            .get('app/dobavi_rezervacije_domacin', {
                headers: {
                    'Authorization': 'Bearer ' + window.localStorage.getItem('jwt')
                }
            })
            .then(response => {
                this.rezervacije = response.data;
            })
            .catch(error => {
                console.log(error);
                alert(error.response.data.sadrzaj);
                if (error.response.status == 400 || error.response.status == 403)   {
                    window.localStorage.removeItem('jwt');
                    window.location = 'login.html';
                }
            });
    },
    methods:    {
        obrisiApartman: function(apartman)  {
            if (confirm('Obriši apartman ' + apartman.id + '?'))    {
                axios
                    .delete('app/obrisi_apartman', {
                        headers: {
                            'Authorization': 'Bearer ' + window.localStorage.getItem('jwt')
                        },
                        data: apartman
                    })
                    .then(response => {
                        if (apartman.status == 0)   {
                            this.aktivni_apartmani = response.data;
                        } else  {
                            this.neaktivni_apartmani = response.data;
                        }
                    })
                    .catch(error => {
                        console.log(error);
                        alert(error.response.data.sadrzaj);
                        if (error.response.status == 400 || error.response.status == 403)   {
                            window.localStorage.removeItem('jwt');
                            window.location = 'login.html';
                        }
                    });
            }
        },

        prikaziAdresu: function(apartman)   {
            return apartman.lokacija.adresa['ulica'] + " " + apartman.lokacija.adresa['broj'] + "\n" + apartman.lokacija.adresa['mesto'] + "\n" + apartman.lokacija.adresa['postanskiBroj'];
        },

        prikaziSliku: function(slika)    {
            //return atob(slika);
            return "data:image/jpeg;base64," + slika;
        },

        izmeniApartman: function(apartman)  {
            window.localStorage.setItem('apartman', JSON.stringify(apartman));
            window.localStorage.setItem('uloga', 'DOMACIN');
            window.location = 'izmena_apartmana.html';
        },

        rastuce: function (a, b) {
            if ( a.cenaPoNoci < b.cenaPoNoci )  {
              return -1;
            }
            if ( a.cenaPoNoci > b.cenaPoNoci )  {
              return 1;
            }
            return 0;
        },

        opadajuce: function(a, b)   {
            if ( a.cenaPoNoci > b.cenaPoNoci )  {
                return -1;
              }
              if ( a.cenaPoNoci < b.cenaPoNoci )  {
                return 1;
              }
              return 0;
        },

        sortirajPoCeni: function(event)  {
            if (this.sortiranje == 'rastuce')   {
                this.aktivni_apartmani.sort(this.rastuce);
            } else  {
                this.aktivni_apartmani.sort(this.opadajuce);
            }
        },

        capitalize: function(string)    {
            return string.charAt(0).toUpperCase() + string.slice(1);
        },

        promeniStatusRezervacije: function(rezervacija)    {


        	rezervacija.status = '3'

        	let datum = new Date(rezervacija.pocetniDatum)
        	rezervacija.pocetniDatum = datum.getTime()

            axios
                .put('app/promeni_status_rezervacije', rezervacija, {
                    headers:    {
                        'Content-Type': 'application/json',
                        'Authorization': 'Bearer ' + window.localStorage.getItem('jwt')
                    }
                })
                .then(response => {
                    this.$refs.msg.classList.remove("error-msg");
                    this.$refs.msg.innerHTML = response.data.sadrzaj;
                })
                .catch(error => {
                    console.log(error);
                    this.$refs.msg.classList.add("error-msg");
                    this.$refs.msg.innerHTML = error.response.data.sadrzaj;
                    if (error.response.status == 400 || error.response.status == 403)   {
                        window.localStorage.removeItem('apartman');
                        window.localStorage.removeItem('jwt');
                        window.location = 'login.html';
                    }
                });
        },

        promeniStatusRezervacije1: function(rezervacija)    {


        	rezervacija.status = '1'
        	let datum = new Date(rezervacija.pocetniDatum)
        	rezervacija.pocetniDatum = datum.getTime()

            axios
                .put('app/promeni_status_rezervacije', rezervacija, {
                    headers:    {
                        'Content-Type': 'application/json',
                        'Authorization': 'Bearer ' + window.localStorage.getItem('jwt')
                    }
                })
                .then(response => {
                    this.$refs.msg.classList.remove("error-msg");
                    this.$refs.msg.innerHTML = response.data.sadrzaj;
                })
                .catch(error => {
                    console.log(error);
                    this.$refs.msg.classList.add("error-msg");
                    this.$refs.msg.innerHTML = error.response.data.sadrzaj;
                    if (error.response.status == 400 || error.response.status == 403)   {
                        window.localStorage.removeItem('apartman');
                        window.localStorage.removeItem('jwt');
                        window.location = 'login.html';
                    }
                });
        },

        promeniStatusRezervacije2: function(rezervacija)    {


        	rezervacija.status = '4'
        	let datum = new Date(rezervacija.pocetniDatum)
        	rezervacija.pocetniDatum = datum.getTime()

            axios
                .put('app/promeni_status_rezervacije', rezervacija, {
                    headers:    {
                        'Content-Type': 'application/json',
                        'Authorization': 'Bearer ' + window.localStorage.getItem('jwt')
                    }
                })
                .then(response => {
                    this.$refs.msg.classList.remove("error-msg");
                    this.$refs.msg.innerHTML = response.data.sadrzaj;
                })
                .catch(error => {
                    console.log(error);
                    this.$refs.msg.classList.add("error-msg");
                    this.$refs.msg.innerHTML = error.response.data.sadrzaj;
                    if (error.response.status == 400 || error.response.status == 403)   {
                        window.localStorage.removeItem('apartman');
                        window.localStorage.removeItem('jwt');
                        window.location = 'login.html';
                    }
                });
        },
        
        prikaziStatus: function(status)   {
            switch (status) {
                case '0':
                    return 'Kreirana';
                case '1':
                    return 'Odbijena';
                case '2':
                    return 'Odustanak';
                case '3':
                    return 'Prihvaćena';
                case '4':
                    return 'Završena';
                default:
                    return 'Invalid status';
            }
        },

        prikaziPoruku: function(poruka) {
            this.$refs.poruka.innerText = "Poruka: \n" + poruka;
        },

        rastuceRezervacije: function (a, b) {
            if ( a.cena < b.cena )  {
              return -1;
            }
            if ( a.cena > b.cena )  {
              return 1;
            }
            return 0;
        },

        opadajuceRezervacije: function(a, b)   {
            if ( a.cena > b.cena )  {
                return -1;
              }
              if ( a.cena < b.cena )  {
                return 1;
              }
              return 0;
        },

        sortirajRezervacijePoCeni: function(event)  {
            if (this.sortiranje_rezervacije == 'rastuce')   {
                this.rezervacije.sort(this.rastuceRezervacije);
            } else  {
                this.rezervacije.sort(this.opadajuceRezervacije);
            }
        }
    },

    computed:    {
        filtriraniApartmani: function() {
            return this.aktivni_apartmani.filter((apartman) => {
                var result = this.pretraga_sadrzaji.every(function(val) {

                    return apartman.idSadrzaja.indexOf(val) >= 0;
                    //return this.pretraga_sadrzaji.some(sadrzaj => sadrzaj.id === val);
                  
                });
                  
                return ((apartman.tip == this.pretraga_tip || this.pretraga_tip == '2') && result);
            });
        },

        filtriraneRezervacije: function(){
    		return this.rezervacije.filter((rezervacija) => {




                return (rezervacija.korisnickoImeGosta.match(this.pretraga_rezervacije))
                		&& (rezervacija.status == this.pretraga_status || this.pretraga_status == '5');
            });
        }
    },

    filters: {
    	dateFormat: function (value, format) {
    		var parsed = moment(value);
    		return parsed.format(format);
    	}
   	}
});
