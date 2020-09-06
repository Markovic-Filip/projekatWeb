new Vue({
    el: '#rezervacija-app',
    data:   {
        apartman: {},
        sadrzaji: [],
        pocetniDatum: new Date(parseInt(Date.now() + 86400000)),
        brojNocenja: 1,
        poruka: '',
        valid: true
    },
    mounted()   {
        this.apartman = JSON.parse(window.localStorage.getItem('apartman'));
        if (this.apartman == null)   {
            document.location.href = 'http://localhost:8080/apartmani.html';
        }/* else  {
            window.localStorage.removeItem('apartman'); // Premestio sam ovo posle ajax poziva za slanje rezervacije
        }*/

        // TODO: axios.get('app/dobavi_sadrzaje')
    },
    components: {
        vuejsDatepicker
    },
    methods:    {
        validacija: function()  {

            this.valid = true;

            this.pocetniDatum.setHours(this.apartman.vremeZaPrijavu);

            if (this.brojNocenja < 1)    {
                this.$refs.broj_nocenja.classList.remove("dobra-vrednost");
                this.$refs.broj_nocenja.classList.add("losa-vrednost");
                this.valid = false;
            } else  {
                if (this.$refs.broj_nocenja.classList.contains("losa-vrednost"))   {
                    this.$refs.broj_nocenja.classList.remove("losa-vrednost");
                    this.$refs.broj_nocenja.classList.add("dobra-vrednost");
                }
            }

            if (this.pocetniDatum.getTime() < Date.now())   {
                alert('Izabran početni datum nije validan.');
                this.valid = false;
            }

            if (this.valid)  {
                this.rezervisi();
            }
        },

        rezervisi: function()   {
            let rezervacija = {
                apartmanId: this.apartman.id,
                pocetniDatum: this.pocetniDatum.getTime(),
                brojNocenja: this.brojNocenja,
                cena: this.apartman.cenaPoNoci * this.brojNocenja,
                poruka: this.poruka
            }

            axios
                .post('app/napravi_rezervaciju', rezervacija, {
                    headers: {
                        'Content-Type': 'application/json',
                        'Authorization': 'Bearer ' + window.localStorage.getItem('jwt')
                    }
                })
                .then(response => {

                })
                .catch(error => {

                });

            window.localStorage.removeItem('apartman'); // Ne treba vise
        },

        prikaziAdresu: function()   {
            return this.apartman.lokacija.adresa['ulica'] + " " + this.apartman.lokacija.adresa['broj'] + ", " + this.apartman.lokacija.adresa['mesto'] + ", " + this.apartman.lokacija.adresa['postanskiBroj'];
        }
    }
});
