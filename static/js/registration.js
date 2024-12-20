new Vue({
    el: '#registration-app',
    data: {
        ime: '',
        prezime: '',
        korisnicko: '',
        lozinka: '',
        potvrdaLozinke: '',
        pol: -1,
        uloga: -1,
        valid: true
    },
    methods: {
        validacija: function()   {
            valid = true;

            // proveri ime
            if (this.$refs.ime.value.length <= 1 || !/^[a-zA-Z]+$/.test(this.$refs.ime.value))   {
                this.$refs.ime.classList.remove("is-valid");
                this.$refs.ime.classList.add("is-invalid");
                valid = false;
            } else  {
                if (this.$refs.ime.classList.contains('is-invalid'))    {
                    this.$refs.ime.classList.remove("is-invalid");
                    this.$refs.ime.classList.add("is-valid");
                }
            }

            // proveri prezime
            if (this.$refs.prezime.value.length <= 1 || !/^[a-zA-Z]+$/.test(this.$refs.prezime.value))   {
                this.$refs.prezime.classList.remove("is-valid");
                this.$refs.prezime.classList.add("is-invalid");
                valid = false;
            } else  {
                if (this.$refs.prezime.classList.contains('is-invalid'))    {
                    this.$refs.prezime.classList.remove("is-invalid");
                    this.$refs.prezime.classList.add("is-valid");
                }
            }

            // proveri korisnicko ime
            // /\W/ znaci sve osim slova, brojeva i underscore-a
            if (this.$refs.korisnickoIme.value.length <= 1 || /\W/.test(this.$refs.korisnickoIme.value))   {
                this.$refs.korisnickoIme.classList.remove("is-valid");
                this.$refs.korisnickoIme.classList.add("is-invalid");
                valid = false;
            } else  {
                if (this.$refs.korisnickoIme.classList.contains('is-invalid'))    {
                    this.$refs.korisnickoIme.classList.remove("is-invalid");
                    this.$refs.korisnickoIme.classList.add("is-valid");
                }
            }

            // provera lozinke
            if (this.$refs.lozinka.value.length < 8 || !this.uporediLozinke() || !/^[0-9a-zA-Z]+$/.test(this.$refs.lozinka.value))   {
                this.$refs.lozinka.classList.remove("is-valid");
                this.$refs.lozinka.classList.add("is-invalid");
                this.$refs.potvrdaLozinke.classList.remove("is-valid");
                this.$refs.potvrdaLozinke.classList.add("is-invalid");
                valid = false;
            } else  {
                if (this.$refs.lozinka.classList.contains('is-invalid'))    {
                    this.$refs.lozinka.classList.remove("is-invalid");
                    this.$refs.lozinka.classList.add("is-valid");
                }
                if (this.$refs.potvrdaLozinke.classList.contains('is-invalid'))    {
                    this.$refs.potvrdaLozinke.classList.remove("is-invalid");
                    this.$refs.potvrdaLozinke.classList.add("is-valid");
                }
            }

            // provera combo box-eva
            if (this.$refs.pol.value == "") {
                this.$refs.pol.classList.remove("is-valid");
                this.$refs.pol.classList.add("is-invalid");
                valid = false;
            } else {
                if (this.$refs.pol.classList.contains('is-invalid'))    {
                    this.$refs.pol.classList.remove("is-invalid");
                    this.$refs.pol.classList.add("is-valid");
                }
            }

            /*
            if (this.$refs.uloga.value == "")   {
                this.$refs.uloga.classList.remove("is-valid");
                this.$refs.uloga.classList.add("is-invalid");
                valid = false;
            } else {
                if (this.$refs.uloga.classList.contains('is-invalid'))    {
                    this.$refs.uloga.classList.remove("is-invalid");
                    this.$refs.uloga.classList.add("is-valid");
                }
            }
            */

            if (valid)  {
                this.registruj();
            }
        },

        uporediLozinke: function()  {
            if (this.$refs.potvrdaLozinke.value.length != this.$refs.lozinka.value.length)  {
                return false;
            }

            for (let i = 0; i < this.$refs.potvrdaLozinke.value.length; i++)  {
                if (this.$refs.potvrdaLozinke.value[i] != this.$refs.lozinka.value[i])   {
                    return false;
                }
            }

            return true;
        },

        registruj: function()   {
        	var korisnik = {
                'ime': this.ime,
                'prezime': this.prezime,
                'korisnickoIme': this.korisnicko,
                'lozinka': this.lozinka,
                'pol': this.pol
            };

            if (!this.ulogovanKorisnik()) {

                //var putanja = 'app/registracija/' + (this.uloga == 1 ? 'domacin' : 'gost');
                let putanja = 'app/registracija/gost';

                axios
                    .post(putanja, korisnik, {
                        headers: {
                            'Content-Type': 'application/json'
                        }
                    })
                    .then(response => {
                        if (response.data.hasOwnProperty('korisnickoIme'))  {
                            //this.$refs.msg.classList.remove("error-msg");
                            //this.$refs.msg.classList.add("ok-msg");
                            //this.$refs.msg.innerHTML = 'Korisnik ' + response.data.korisnickoIme + ' uspešno registrovan!';
                            //document.getElementById('registForma').reset();
                            window.localStorage.setItem('jwt', response.data.JWTToken);
                            window.location = "gost_page.html";
                        } else  {
                            console.log(response);
                        }
                    })
                    .catch(error => {
                        console.log(error);
                        //alert(error.response.data.sadrzaj);
                        this.$refs.msg.classList.add("error-msg");
                        this.$refs.msg.innerHTML = error.response.data.sadrzaj;
                    });
            } else  {
                let putanja = 'app/registracija/domacin';

                axios
                    .post(putanja, korisnik, {
                        headers: {
                            'Content-Type': 'application/json'
                        }
                    })
                    .then(response => {
                        if (response.data.hasOwnProperty('korisnickoIme'))  {
                            this.$refs.msg.classList.add("ok-msg");
                            this.$refs.msg.innerHTML = "Domaćin " + response.data.korisnickoIme + " uspešno registrovan!";
                        }
                    })
                    .catch(error => {
                        console.log(error);
                        this.$refs.msg.classList.add("error-msg");
                        this.$refs.msg.innerHTML = error.response.data.sadrzaj;
                    });
            }
        },

        ulogovanKorisnik: function() {
            return window.localStorage.getItem('jwt') != null;
        }
    }
});
