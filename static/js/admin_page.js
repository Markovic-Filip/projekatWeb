new Vue({
    el: '#admin-app',
    data:   {
        //uloga: '',
        korisnici: null
    },
    mounted()   {
        /*
        axios
            .get('app/preuzmi_ulogu', {
                headers: {
                    'Authorization': 'Bearer ' + window.localStorage.getItem('jwt')
                }
            })
            .then(response => {
                this.uloga = response.data.sadrzaj;
            })
            .catch(error => {
                console.log(error);
                window.localStorage.removeItem('jwt');
                window.location = 'login.html';
            });
            */
        axios
            .get('app/dobavi_korisnike', {
                headers: {
                    'Authorization': 'Bearer ' + window.localStorage.getItem('jwt')
                }
            })
            .then(response => {
                this.korisnici = response.data;
            })
            .catch(error => {
                console.log(error);
                // TODO: Zakasni prelazak na login stranicu par sekundi da se poruka lepo vidi (ukoliko dodje do greske)
                alert(error.response.data.sadrzaj);
                setTimeout(() => {
                    window.localStorage.removeItem('jwt');
                    window.location = 'login.html';
                }, 5000);
            });
    },
    methods:    {
        obrisiKorisnika: function(korisnik) {
            axios
                .delete('app/obrisi_korisnika', {
                    headers: {
                        'Authorization': 'Bearer ' + window.localStorage.getItem('jwt');
                    }
                })
                .then(response => {
                    this.korisnici = response.data;
                    //location.reload();
                })
                .catch(error => {
                    console.log(error);
                    alert(error.response.data.sadrzaj);
                    if (error.response.status == 400 || error.response.status == 500)   {
                        setTimeout(() => {
                            window.localStorage.removeItem('jwt');
                            window.location = 'login.html';
                        }, 5000);
                    } else  {
                        setTimeout(() => {
                            location.reload();
                        })
                    }
                });
        }
    }
});