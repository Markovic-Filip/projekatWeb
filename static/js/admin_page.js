new Vue({
    el: '#admin-app',
    data:   {
        //uloga: '',
        korisnici: [],
        backup_korisnici: [],
        sadrzaji: [],
        pretraga_kor_ime: "",
        pretraga_uloga: 5,
        pretraga_pol: 5,

        pretraga: '',
        pretraga_tip: '2',
        pretraga_cena: '0',
        pretraga_broj_soba: '0',
        pretraga_broj_gostiju: undefined,
        pretraga_sadrzaji: [],
        sortiranje: undefined,

        rezervacije: [],
        sortiranje_rezervacije: undefined,
        pretraga_rezervacije: '',
        pretraga_status: '5',

        sadrzaj: '',

        aktivni_apartmani: [],
        neaktivni_apartmani: []
    },
    mounted()   {
        /*
        axios
            .get('app/preuzmi_ulogu', {
                headers: {
                    'Authorization': 'Bearer ' + window.localStorage.getItem('jwt')
                }
            })
            .then(response => {
                this.uloga = response.data.sadrzaj;
            })
            .catch(error => {
                console.log(error);
                window.localStorage.removeItem('jwt');
                window.location = 'login.html';
            });
            */
        axios
            .get('app/dobavi_korisnike', {
                headers: {
                    'Authorization': 'Bearer ' + window.localStorage.getItem('jwt')
                }
            })
            .then(response => {
                this.korisnici = response.data;
                //this.backup_korisnici = response.data; // !!! Ista referenca
                for (korisnik of this.korisnici) {
                    this.backup_korisnici.push(korisnik);
                }
            })
            .catch(error => {
                console.log(error);
                alert(error.response.data.sadrzaj);
                if (error.response.status == 400 || error.response.status == 403)   {
                    // Zakasni prelazak na login stranicu par sekundi da se poruka lepo vidi (ukoliko dodje do greske)
                    // UPDATE: Nema potrebe za kasnjenjem jer nece preci na login.html dokle god korisnik ne pritisne ok na alert poruci
                    //setTimeout(() => {
                        window.localStorage.removeItem('jwt');
                        window.location = 'login.html';
                    //}, 5000);
                }
            });

        axios
            .get('app/dobavi_rezervacije', {
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
                console.log(error);
                alert(error.response.data.sadrzaj);
            });
    },
    methods:    {
        obrisiKorisnika: function(korisnik) {
            if (confirm("Obriši korisnika " + korisnik.korisnickoIme + "?"))  {
                axios
                    .delete('app/obirsi_korisnika', {
                        headers: {
                            'Authorization': 'Bearer ' + window.localStorage.getItem('jwt')
                        },
                        data: korisnik
                    })
                    .then(response => {
                        this.korisnici = response.data;
                        //location.reload(); // nema potrebe za ovim, tabela se sama azurira
                    })
                    .catch(error => {
                        console.log(error);
                        alert(error.response.data.sadrzaj);
                        if (error.response.status == 400 || error.response.status == 403)   {
                            // Zakasni prelazak na login stranicu par sekundi da se poruka lepo vidi (ukoliko dodje do greske)
                            // UPDATE: Nema potrebe za kasnjenjem jer nece preci na login.html dokle god korisnik ne pritisne ok na alert poruci
                            //setTimeout(() => {
                                window.localStorage.removeItem('jwt');
                                window.location = 'login.html';
                            //}, 5000);
                        }
                    });
            }
        },

        promeniStatus: function(korisnik)   {
            let poruka = (korisnik.status === '0' ? 'Blokiraj ' : 'Aktiviraj ') + 'korisnika ' + korisnik.korisnickoIme + '?';
            if (confirm(poruka))    {
                axios
                    .put('app/promeni_status_korisnika', korisnik, {
                        headers: {
                            'Authorization': 'Bearer ' + window.localStorage.getItem('jwt')
                        }
                    })
                    .then(response => {
                        this.korisnici = response.data;
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

        filter: function()   {
            let vrednost = this.pretraga_kor_ime.toLowerCase();
            let filtrirani_korisnici = [];
            for (korisnik of this.backup_korisnici)    {
                // Ako korisnickoIme sadrzi string iz input polja && (ako se korisnikova uloga poklapa sa trazenom || uloga nije izabrana (dakle uzimaj sve uloge u obzir) && (isto kao za ulogu samo za pol))
                if (korisnik.korisnickoIme.toLowerCase().includes(vrednost) && (korisnik.uloga === this.pretraga_uloga || this.pretraga_uloga == 5) && (korisnik.pol === this.pretraga_pol || this.pretraga_pol == 5))    {
                    filtrirani_korisnici.push(korisnik);
                }
            }

            this.korisnici = filtrirani_korisnici;
        },

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

        izmeniApartman: function(apartman)  {
            window.localStorage.setItem('apartman', JSON.stringify(apartman));
            window.localStorage.setItem('uloga', 'ADMINISTRATOR');
            window.location = 'izmena_apartmana.html';
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

        prikaziAdresu: function(apartman)   {
            //return apartman.lokacija.adresa['mesto'] + ", " + apartman.lokacija.adresa['ulica'] + " " + apartman.lokacija.adresa['broj'];
            return apartman.lokacija.adresa['ulica'] + " " + apartman.lokacija.adresa['broj'] + "\n" + apartman.lokacija.adresa['mesto'] + "\n" + apartman.lokacija.adresa['postanskiBroj'];
        },

        prikaziSliku: function(slika)    {
            //return atob(slika);
            if (slika != null)  {
                return "data:image/jpeg;base64," + slika;
            } else  {
                return '';
            }
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

        obrisiSadrzaj: function(sadrzaji)  {
            if (confirm('Obriši sadžaj ' + sadrzaji.naziv + '?'))    {
                axios
                    .delete('app/obrisi_sadrzaj_apartmana', {
                        headers: {
                            'Authorization': 'Bearer ' + window.localStorage.getItem('jwt')
                        },
                        data: sadrzaji
                    })
                    .then(response => {
                        this.sadrzajApartmana=response.data;
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

        validacija: function() {
        	valid = true;
         // proveri sadrzaj
            if (this.$refs.sadrzaj.value.length <= 1 || !/^[a-zA-Z ]+$/.test(this.$refs.sadrzaj.value))   {
                this.$refs.sadrzaj.classList.remove("is-valid");
                this.$refs.sadrzaj.classList.add("is-invalid");
                valid = false;
            } else  {
                if (this.$refs.sadrzaj.classList.contains('is-invalid'))    {
                    this.$refs.sadrzaj.classList.remove("is-invalid");
                    this.$refs.sadrzaj.classList.add("is-valid");
                }
            }




            if(valid) {
            	this.dodajSadrzaj();
            }

        	},

        	dodajSadrzaj: function(){


        		var sadrzajiApartmana = {
        				'naziv' : this.sadrzaj,
        				'id' : -1
        				};

        		this.sadrzajApartmana.push(sadrzajApartmana)
        		let putanja = 'app/dodavanje_sadrzaja';

                axios
                    .post(putanja, sadrzajiApartmana, {
                        headers: {
                            'Content-Type': 'application/json',
                            'Authorization': 'Bearer ' + window.localStorage.getItem('jwt')
                        }
                    })
                    .then(response => {

                    	this.sadrzajApartmana=response.data;

                    })
                    .catch(error => {
                        console.log(error);
                        //alert(error.response.data.sadrzaj);
                        this.$refs.msg.classList.add("error-msg");
                        this.$refs.msg.innerHTML = error.response.data.sadrzaj;
                    });





            },
            
            sacuvajIzmene: function()   {
                axios
                .put('app/izmeni_sadrzaj_apartmana', this.sadrzajApartmana, {
                    headers:    {
                        'Content-Type': 'application/json',
                        'Authorization': 'Bearer ' + window.localStorage.getItem('jwt')
                    }
                })
                .then(response => {
                    alert(response.data.sadrzaj);
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

    computed:   {
        filtriraniApartmani: function() {
            return this.aktivni_apartmani.filter((apartman) => {
                let gornja_granica_cena = 0;
                let donja_granica_cena = 0;
                let sve_cene = false;
                switch (this.pretraga_cena) {
                    case '0':
                        sve_cene = true;
                        break;
                    case '1':
                        gornja_granica_cena = 5;
                        donja_granica_cena = 0;
                        break;
                    case '2':
                        gornja_granica_cena = 10;
                        donja_granica_cena = 5;
                        break;
                    case '3':
                        gornja_granica_cena = 20;
                        donja_granica_cena = 10;
                        break;
                    case '4':
                        gornja_granica_cena = 30;
                        donja_granica_cena = 20;
                        break;
                    case '5':
                        gornja_granica_cena = Number.MAX_SAFE_INTEGER;
                        donja_granica_cena = 30;
                        break;
                    default:
                        break;
                }

                let gornja_granica_sobe = 0;
                let donja_granica_sobe = 0;
                let sve_sobe = false;
                switch (this.pretraga_broj_soba) {
                    case '0':
                        sve_sobe = true;
                        break;
                    case '1':
                        donja_granica_sobe = 0;
                        gornja_granica_sobe = 1;
                        break;
                    case '2':
                        donja_granica_sobe = 1;
                        gornja_granica_sobe = 3;
                        break;
                    case '3':
                        donja_granica_sobe = 3;
                        gornja_granica_sobe = 5;
                        break;
                    case '4':
                        donja_granica_sobe = 5;
                        gornja_granica_sobe = Number.MAX_SAFE_INTEGER;
                        break;
                    default:
                        break;
                }

                var result = this.pretraga_sadrzaji.every(function(val) {

                    return apartman.idSadrzaja.indexOf(val) >= 0;
                    //return this.pretraga_sadrzaji.some(sadrzaj => sadrzaj.id === val);
                  
                });

                return (apartman.lokacija.adresa.mesto.toLowerCase().match(this.pretraga.toLowerCase()) || apartman.lokacija.adresa.ulica.toLowerCase().match(this.pretraga.toLowerCase()))
                    && (apartman.tip == this.pretraga_tip || this.pretraga_tip == '2')
                    && ((apartman.cenaPoNoci > donja_granica_cena && apartman.cenaPoNoci <= gornja_granica_cena) || sve_cene)
                    && ((apartman.brojSoba > donja_granica_sobe && apartman.brojSoba <= gornja_granica_sobe) || sve_sobe)
                    && (apartman.brojGostiju == this.pretraga_broj_gostiju || this.pretraga_broj_gostiju == undefined || this.pretraga_broj_gostiju == 0)
                    && result;
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
