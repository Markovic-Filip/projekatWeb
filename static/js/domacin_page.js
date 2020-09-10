new Vue({
    el: '#domacin-app',
    data:   {
        aktivni_apartmani: [],
        neaktivni_apartmani: []
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

        izmeniApartman: function(apartman)  {
            window.localStorage.setItem('apartman', JSON.stringify(apartman));
            window.localStorage.setItem('uloga', 'DOMACIN');
            window.location = 'http://localhost:8080/izmena_apartmana.html';
        }
    }
});
