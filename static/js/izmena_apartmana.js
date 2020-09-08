new Vue({
    el: '#izmena-apartmana-app',
    data:   {
        apartman: {},
        sadrzaji: [],
        valid: false
    },
    mounted()   {
        this.apartman = JSON.parse(window.localStorage.getItem('apartman'));
        if (this.apartman == null)   {
            if (this.uloga === 'DOMACIN')    {
                document.location.href = 'http://localhost:8080/domacin_page.html';
            } else if (this.uloga === 'ADMINISTRATOR')  {
                document.location.href = 'http://localhost:8080/admin_page.html';
            } else  {
                window.localStorage.removeItem('jwt');
                document.location.href = 'http://localhost:8080/login.html';
            }
        }

        // TODO: axios.get('app/dobavi_sadrzaje')
    },
    methods:    {
        uloga: function() {
            let uloga = window.localStorage.getItem('uloga');
            if (uloga != null)  {
                return uloga;
            } else  {
                window.localStorage.removeItem('jwt');
                document.location.href = 'http://localhost:8080/login.html';
            }
        },

        obrisiSadrzaj: function(sadrzaj)   {
            this.apartman.idSadrzaja.splice(indexOf(sadrzaj));
        },

        promeniStatus: function()   {
            if (this.apartman.status === '1')    {
                this.apartman.status = '0';
            } else  {
                this.apartman.status = '1';
            }
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
        }
    }
});
