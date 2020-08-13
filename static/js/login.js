new Vue({
    el: '#login-app',
    data:   {
        korisnicko: '',
        lozinka: '',
        valid: true
    },
    methods:    {
        validacija: function()  {

            valid = true;

            if (this.$refs.korisnickoIme.value.length == 0)   {
                this.$refs.korisnickoIme.classList.remove("is-valid");
                this.$refs.korisnickoIme.classList.add("is-invalid");
                valid = false;
            } else  {
                if (this.$refs.korisnickoIme.classList.contains('is-invalid'))    {
                    this.$refs.korisnickoIme.classList.remove("is-invalid");
                    this.$refs.korisnickoIme.classList.add("is-valid");
                }
            }

            if (this.$refs.lozinka.value.length == 0)   {
                this.$refs.lozinka.classList.remove("is-valid");
                this.$refs.lozinka.classList.add("is-invalid");
                valid = false;
            } else  {
                if (this.$refs.korisnickoIme.classList.contains('is-invalid'))    {
                    this.$refs.korisnickoIme.classList.remove("is-invalid");
                    this.$refs.korisnickoIme.classList.add("is-valid");
                }
            }

            if (valid)
            {
                this.login();
            }
        },

        login: function()   {
            var putanja = 'app/login';

            let payload = this.korisnicko + '&' + this.lozinka;

            axios
                .post(putanja, payload)
                .then(response => {
                    if (response.data.hasOwnProperty('JWTToken'))   {
                        //this.$refs.msg.classList.remove("error-msg");
                        //alert(response.data.JWTToken);
                        window.localStorage.setItem('jwt', response.data.JWTToken);
                        //window.location = "index.html";
                        let uloga = response.data.uloga;
                        if (uloga == 0) {
                            window.location = "admin_page.html";
                        } else {
                            window.location = "index.html";
                        }
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
        }
    }
})