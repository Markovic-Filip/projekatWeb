new Vue({
    el: '#apartmani-app',
    data:   {
        apartmani: []
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
        }
    }
});
