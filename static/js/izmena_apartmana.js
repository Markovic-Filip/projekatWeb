new Vue({
    el: '#izmena-apartmana-app',
    data:   {
        apartman: {},
        sadrzaji: [],
        sviSadrzaji: [],
        komentari: [],
        selektovanSadrzaj: null,
        valid: false
    },
    mounted()   {
        this.apartman = JSON.parse(window.localStorage.getItem('apartman'));
        if (this.apartman == null)   {
            if (this.uloga === 'DOMACIN')    {
                window.location = 'http://localhost:8080/domacin_page.html';
            } else if (this.uloga === 'ADMINISTRATOR')  {
                window.location = 'http://localhost:8080/admin_page.html';
            } else  {
                window.localStorage.removeItem('jwt');
                window.location = 'http://localhost:8080/login.html';
            }
        }

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
                //alert(error.response.data.sadrzaj);
            });

        axios
            .get('app/dobavi_sadrzaj_apartmana')
            .then(response => {
                this.sviSadrzaji = response.data;
            })
            .catch(error => {
                error.log(error);
            });
        
        axios
        .get('app/dobavi_komentare', {
            params: {
                idApartmana: this.apartman.id,
                domacin: true
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
    methods:    {
        uloga: function() {
            let uloga = window.localStorage.getItem('uloga');
            if (uloga != null)  {
                return uloga;
            } else  {
                window.localStorage.removeItem('jwt');
                window.location = 'http://localhost:8080/login.html';
            }
        },

        obrisiSadrzaj: function(sadrzaj)   {
            let indexSadrzaja = this.apartman.idSadrzaja.indexOf(sadrzaj.id);
            this.apartman.idSadrzaja.splice(indexSadrzaja, 1);
            this.sadrzaji.splice(this.sadrzaji.indexOf(sadrzaj), 1);
        },

        dodajSadrzaj: function()    {
            if (!this.apartman.idSadrzaja.includes(this.selektovanSadrzaj.id))  {
                this.apartman.idSadrzaja.push(this.selektovanSadrzaj.id);
                this.sadrzaji.push(this.selektovanSadrzaj);
            }
        },

        promeniStatus: function()   {
            if (this.apartman.status === '1')    {
                this.apartman.status = '0';
            } else  {
                this.apartman.status = '1';
            }
        },
        
        promeniStatusKomentara: function(komentar)    {
            komentar.odobren = !komentar.odobren;

            axios
                .put('app/promeni_status_komentara', komentar, {
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

        validacija: function()   {
            this.valid = true;

            if (this.apartman.brojSoba < 1)    {
                this.$refs.broj_soba.classList.remove("dobra-vrednost");
                this.$refs.broj_soba.classList.add("losa-vrednost");
                this.valid = false;
            } else  {
                if (this.$refs.broj_soba.classList.contains("losa-vrednost"))   {
                    this.$refs.broj_soba.classList.remove("losa-vrednost");
                    this.$refs.broj_soba.classList.add("dobra-vrednost");
                }
            }

            if (this.apartman.brojGostiju < 1)  {
                this.$refs.broj_gostiju.classList.remove('dobra-vrednost');
                this.$refs.broj_gostiju.classList.add('losa-vrednost');
                this.valid = false;
            } else  {
                if (this.$refs.broj_gostiju.classList.contains('losa-vrednost'))   {
                    this.$refs.broj_gostiju.classList.remove('losa-vrednost');
                    this.$refs.broj_gostiju.classList.add('dobra-vrednost');
                }
            }

            if (this.apartman.cenaPoNoci <= 0)   {
                this.$refs.cena_po_noci.classList.remove('dobra-vrednost');
                this.$refs.cena_po_noci.classList.add('losa-vrednost');
                this.valid = false;
            } else  {
                if (this.$refs.cena_po_noci.classList.contains('losa-vrednost'))   {
                    this.$refs.cena_po_noci.classList.remove('losa-vrednost');
                    this.$refs.cena_po_noci.classList.add('dobra-vrednost');
                }
            }

            if (this.apartman.vremeZaPrijavu < 0 || this.apartman.vremeZaPrijavu > 23 || this.apartman.vremeZaPrijavu == '')   {
                this.$refs.vreme_za_prijavu.classList.remove('dobra-vrednost');
                this.$refs.vreme_za_prijavu.classList.add('losa-vrednost');
                this.valid = false;
            } else  {
                if (this.$refs.vreme_za_prijavu.classList.contains('losa-vrednost'))   {
                    this.$refs.vreme_za_prijavu.classList.remove('losa-vrednost');
                    this.$refs.vreme_za_prijavu.classList.add('dobra-vrednost');
                }
            }

            if (this.apartman.vremeZaOdjavu < 0 || this.apartman.vremeZaOdjavu > 23 || this.apartman.vremeZaOdjavu == '')   {
                this.$refs.vreme_za_odjavu.classList.remove('dobra-vrednost');
                this.$refs.vreme_za_odjavu.classList.add('losa-vrednost');
                this.valid = false;
            } else  {
                if (this.$refs.vreme_za_odjavu.classList.contains('losa-vrednost'))   {
                    this.$refs.vreme_za_odjavu.classList.remove('losa-vrednost');
                    this.$refs.vreme_za_odjavu.classList.add('dobra-vrednost');
                }
            }

            if (this.valid)  {
                this.sacuvajIzmene();
            }
        },

        sacuvajIzmene: function()   {
            axios
            .put('app/izmeni_apartman', this.apartman, {
                headers:    {
                    'Content-Type': 'application/json',
                    'Authorization': 'Bearer ' + window.localStorage.getItem('jwt')
                }
            })
            .then(response => {
                this.$refs.msg.classList.remove("error-msg");
                this.$refs.msg.innerHTML = response.data.sadrzaj;
                window.localStorage.removeItem('apartman'); // Ne treba vise
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

        capitalize: function(string)    {
            return string.charAt(0).toUpperCase() + string.slice(1);
        }
    }
});
