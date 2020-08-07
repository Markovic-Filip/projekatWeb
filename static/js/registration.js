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
        //korisnik: {}
    },
    methods: {
        validacija: function()   {
            //alert(this.ime + ', ' + this.prezime + ', ' + this.korisnicko + ', ' + this.lozinka);

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
            //this.korisnik = {
        	var korisnik = {
                'ime': this.ime,
                'prezime': this.prezime,
                'korisnickoIme': this.korisnicko,
                'lozinka': this.lozinka,
                'pol': this.pol
            };

            //var putanja = 'app/registracija/' + (this.uloga == 1 ? 'domacin' : 'gost');
            var putanja = 'app/registracija/gost';

            axios
                .post(putanja, korisnik, {
                    headers: {
                        'Content-Type': 'application/json'
                    }
                })
                .then(response => {
                    if (response.data.hasOwnProperty('korisnickoIme'))  {
                        alert('Korisnik ' + response.data.korisnickoIme + ' uspešno registrovan!');
                        document.getElementById('registForma').reset();
                    } else  {
                        console.log(response);
                    }
                })
                .catch(error => {
                    console.log(error);
                    alert(error.response.data.sadrzaj);
                });
        }
    }
});