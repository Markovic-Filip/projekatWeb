new Vue({
    el: '#domacin-app',
    data:   {
        aktivni_apartmani: [],
        neaktivni_apartmani: [],
        sadrzaji: [],
        sortiranje: undefined,
        pretraga_tip: '2',
        pretraga_sadrzaji: []
    },
    mounted()  {
        axios
            .get('app/dobavi_aktivne_apartmane', {
                headers: {
                    'Authorization': 'Bearer ' + window.localStorage.getItem('jwt')
                }
            })
            .then(response => {
                this.aktivni_apartmani = response.data;
            })
            .catch(error => {
                console.log(error);
                alert(error.response.data.sadrzaj);
                if (error.response.status == 400 || error.response.status == 403)   {
                    window.localStorage.removeItem('jwt');
                    window.location = 'login.html';
                }
            });
        
        axios
            .get('app/dobavi_neaktivne_apartmane', {
                headers: {
                    'Authorization': 'Bearer ' + window.localStorage.getItem('jwt')
                }
            })
            .then(response => {
                this.neaktivni_apartmani = response.data;
            })
            .catch(error => {
                console.log(error);
                alert(error.response.data.sadrzaj);
                if (error.response.status == 400 || error.response.status == 403)   {
                    window.localStorage.removeItem('jwt');
                    window.location = 'login.html';
                }
            });

        axios
            .get('app/dobavi_sadrzaj_apartmana')
            .then(response => {
                this.sadrzaji = response.data;
            })
            .catch(error => {
                alert(error.response.data.sadrzaj);
            });
    },
    methods:    {
        obrisiApartman: function(apartman)  {
            if (confirm('Obriši apartman ' + apartman.id + '?'))    {
                axios
                    .delete('app/obrisi_apartman', {
                        headers: {
                            'Authorization': 'Bearer ' + window.localStorage.getItem('jwt')
                        },
                        data: apartman
                    })
                    .then(response => {
                        if (apartman.status == 0)   {
                            this.aktivni_apartmani = response.data;
                        } else  {
                            this.neaktivni_apartmani = response.data;
                        }
                    })
                    .catch(error => {
                        console.log(error);
                        alert(error.response.data.sadrzaj);
                        if (error.response.status == 400 || error.response.status == 403)   {
                            window.localStorage.removeItem('jwt');
                            window.location = 'login.html';
                        }
                    });
            }
        },

        prikaziAdresu: function(apartman)   {
            return apartman.lokacija.adresa['ulica'] + " " + apartman.lokacija.adresa['broj'] + "\n" + apartman.lokacija.adresa['mesto'] + "\n" + apartman.lokacija.adresa['postanskiBroj'];
        },

        prikaziSliku: function(slika)    {
            //return atob(slika);
            return "data:image/jpeg;base64," + slika;
        },

        izmeniApartman: function(apartman)  {
            window.localStorage.setItem('apartman', JSON.stringify(apartman));
            window.localStorage.setItem('uloga', 'DOMACIN');
            window.location = 'izmena_apartmana.html';
        },

        rastuce: function (a, b) {
            if ( a.cenaPoNoci < b.cenaPoNoci )  {
              return -1;
            }
            if ( a.cenaPoNoci > b.cenaPoNoci )  {
              return 1;
            }
            return 0;
        },

        opadajuce: function(a, b)   {
            if ( a.cenaPoNoci > b.cenaPoNoci )  {
                return -1;
              }
              if ( a.cenaPoNoci < b.cenaPoNoci )  {
                return 1;
              }
              return 0;
        },

        sortirajPoCeni: function(event)  {
            if (this.sortiranje == 'rastuce')   {
                this.aktivni_apartmani.sort(this.rastuce);
            } else  {
                this.aktivni_apartmani.sort(this.opadajuce);
            }
        },

        capitalize: function(string)    {
            return string.charAt(0).toUpperCase() + string.slice(1);
        }
    },

    computed:    {
        filtriraniApartmani: function() {
            return this.aktivni_apartmani.filter((apartman) => {
                var result = this.pretraga_sadrzaji.every(function(val) {

                    return apartman.idSadrzaja.indexOf(val) >= 0;
                    //return this.pretraga_sadrzaji.some(sadrzaj => sadrzaj.id === val);
                  
                });
                  
                return ((apartman.tip == this.pretraga_tip || this.pretraga_tip == '2') && result);
            });
        }
    }
});
