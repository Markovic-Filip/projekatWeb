new Vue({
    el: '#profil-app',
    data: {
        uloga: '',
        korisnik: {},
        stara_lozinka: '',
        nova_lozinka: '',
        kontrolna_lozinka: ''
    },
    mounted()   {
        this.uloga = window.localStorage.getItem('uloga');
        if (this.uloga == null) {
            window.localStorage.removeItem('uloga');
            window.localStorage.removeItem('jwt');
            window.location = 'login.html';
        }

        axios
            .get('app/dobavi_korisnika', {
                headers: {
                    'Authorization': 'Bearer ' + window.localStorage.getItem('jwt')
                }
            })
            .then(response => {
                this.korisnik = response.data;
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
        validacija: function()  {
            let valid = true;

            // proveri ime
            if (this.korisnik.ime.length <= 1 || !/^[a-zA-Z]+$/.test(this.korisnik.ime))   {
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
            if (this.korisnik.prezime.length <= 1 || !/^[a-zA-Z]+$/.test(this.korisnik.prezime))   {
                this.$refs.prezime.classList.remove("is-valid");
                this.$refs.prezime.classList.add("is-invalid");
                valid = false;
            } else  {
                if (this.$refs.prezime.classList.contains('is-invalid'))    {
                    this.$refs.prezime.classList.remove("is-invalid");
                    this.$refs.prezime.classList.add("is-valid");
                }
            }

            // proveri lozinke
            if (this.nova_lozinka.length == 0 && this.kontrolna_lozinka.length == 0)    {
                if (this.$refs.novaLozinka.classList.contains("is-invalid"))   {
                    this.$refs.novaLozinka.classList.remove("is-invalid");
                }

                if (this.$refs.kontrolnaLozinka.classList.contains("is-invalid"))  {
                    this.$refs.kontrolnaLozinka.classList.remove("is-invalid");
                }
            } else {
                if (this.stara_lozinka == this.korisnik.lozinka)    {
                    if (this.$refs.staraLozinka.classList.contains("is-invalid"))   {
                        this.$refs.staraLozinka.classList.remove("is-invalid");
                        this.$refs.staraLozinka.classList.add("is-valid");
                    }

                    if (this.nova_lozinka != this.kontrolna_lozinka)   {
                        this.$refs.novaLozinka.classList.remove("is-valid");
                        this.$refs.novaLozinka.classList.add("is-invalid");
                        this.$refs.kontrolnaLozinka.classList.remove("is-valid");
                        this.$refs.kontrolnaLozinka.classList.add("is-invalid");
                        valid = false;
                    } else  {
                        if (this.$refs.novaLozinka.classList.contains("is-invalid"))   {
                            this.$refs.novaLozinka.classList.remove("is-invalid");
                            this.$refs.novaLozinka.classList.add("is-valid");
                        }

                        if (this.$refs.kontrolnaLozinka.classList.contains("is-invalid"))  {
                            this.$refs.kontrolnaLozinka.classList.remove("is-invalid");
                            this.$refs.kontrolnaLozinka.classList.add("is-valid");
                        }

                        this.korisnik.lozinka = this.nova_lozinka;
                    }
                } else  {
                    this.$refs.staraLozinka.classList.remove("is-valid");
                    this.$refs.staraLozinka.classList.add("is-invalid");
                    valid = false;
                }
            }

            if (valid)  {
                this.izmeniKorisnika();
            }
        },

        izmeniKorisnika: function() {
            axios
                .put('app/izmeni_korisnika', this.korisnik, {
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
                        window.localStorage.removeItem('jwt');
                        window.location = 'login.html';
                    }
                });
        }
    }
});