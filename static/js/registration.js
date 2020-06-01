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
            if (this.$refs.ime.value.length <= 1)   {
                this.$refs.ime.classList.remove("is-valid");
                this.$refs.ime.classList.add("is-invalid");
                valid = false;
            } else  {
                this.$refs.ime.classList.remove("is-invalid");
                this.$refs.ime.classList.add("is-valid");
            }

            // proveri prezime
            if (this.$refs.prezime.value.length <= 1)   {
                this.$refs.prezime.classList.remove("is-valid");
                this.$refs.prezime.classList.add("is-invalid");
                valid = false;
            } else  {
                this.$refs.prezime.classList.remove("is-invalid");
                this.$refs.prezime.classList.add("is-valid");
            }

            // proveri korisnicko ime
            if (this.$refs.korisnickoIme.value.length <= 1)   {
                this.$refs.korisnickoIme.classList.remove("is-valid");
                this.$refs.korisnickoIme.classList.add("is-invalid");
                valid = false;
            } else  {
                this.$refs.korisnickoIme.classList.remove("is-invalid");
                this.$refs.korisnickoIme.classList.add("is-valid");
            }

            // provera lozinke
            if (this.$refs.lozinka.value.length < 8 || !this.uporediLozinke())   {
                this.$refs.lozinka.classList.remove("is-valid");
                this.$refs.lozinka.classList.add("is-invalid");
                this.$refs.potvrdaLozinke.classList.remove("is-valid");
                this.$refs.potvrdaLozinke.classList.add("is-invalid");
                valid = false;
            } else  {
                this.$refs.lozinka.classList.remove("is-invalid");
                this.$refs.lozinka.classList.add("is-valid");
                this.$refs.potvrdaLozinke.classList.remove("is-invalid");
                this.$refs.potvrdaLozinke.classList.add("is-valid");
            }

            // provera combo box-eva
            if (this.$refs.pol.value == "") {
                this.$refs.pol.classList.remove("is-valid");
                this.$refs.pol.classList.add("is-invalid");
                valid = false;
            } else {
                this.$refs.pol.classList.remove("is-invalid");
                this.$refs.pol.classList.add("is-valid");
            }

            if (this.$refs.uloga.value == "")   {
                this.$refs.uloga.classList.remove("is-valid");
                this.$refs.uloga.classList.add("is-invalid");
                valid = false;
            } else {
                this.$refs.uloga.classList.remove("is-invalid");
                this.$refs.uloga.classList.add("is-valid");
            }

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

            var putanja = 'app/registracija/' + (this.uloga == 1 ? 'domacin' : 'gost');

            // TODO: ukoliko registracija nije uspesna...
            axios
                .post(putanja, korisnik, {
                    headers: {
                        'Content-Type': 'application/json'
                    }
                })
                .then(response => (alert('Korisnik ' + response.data.korisnickoIme + ' je uspešno registrovan!')))
                .catch(error => (console.log(error)))
        }
    }
});