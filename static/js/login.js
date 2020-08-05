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
                .post(putanja, payload, {
                    // headers: {
                    //     'Content-Type': 'application/json'
                    // }
                })
                .then(response => {
                    
                })
                .catch(error => (console.log(error)))
        }
    }
})