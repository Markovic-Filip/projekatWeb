new Vue({
    el: '#apartmani-app',
    data:   {
        apartmani: [],
        pretraga: '',
        pretraga_tip: '2',
        pretraga_cena: '0',
        pretraga_broj_soba: '0',
        pretraga_broj_gostiju: undefined
    },
    mounted()  {
        axios
            .get('app/dobavi_aktivne_apartmane')
            .then(response => {
                this.apartmani = response.data;
            })
            .catch(error => {
                console.log(error);
                alert(error.response.data.sadrzaj);
            });
    },
    methods:    {
        prikaziAdresu: function(apartman)   {
            return apartman.lokacija.adresa['ulica'] + " " + apartman.lokacija.adresa['broj'] + "\n" + apartman.lokacija.adresa['mesto'] + "\n" + apartman.lokacija.adresa['postanskiBroj'];
        },

        ulogovanKorisnik: function()    {
            return window.localStorage.getItem('jwt') != null;
        },

        rezervisiApartman: function(apartman)   {
            window.localStorage.setItem('apartman', JSON.stringify(apartman));
            window.location = 'http://localhost:8080/rezervacija_apartmana.html';
        }
    },
    computed:   {
        filtriraniApartmani: function() {
            return this.apartmani.filter((apartman) => {
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

                return (apartman.lokacija.adresa.mesto.match(this.pretraga) || apartman.lokacija.adresa.ulica.match(this.pretraga))
                    && (apartman.tip == this.pretraga_tip || this.pretraga_tip == '2')
                    && ((apartman.cenaPoNoci > donja_granica_cena && apartman.cenaPoNoci <= gornja_granica_cena) || sve_cene)
                    && ((apartman.brojSoba > donja_granica_sobe && apartman.brojSoba <= gornja_granica_sobe) || sve_sobe)
                    && (apartman.brojGostiju == this.pretraga_broj_gostiju || this.pretraga_broj_gostiju == undefined || this.pretraga_broj_gostiju == 0);
            });
        }
    }
});
