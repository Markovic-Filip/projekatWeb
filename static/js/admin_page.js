new Vue({
    el: '#admin-app',
    data:   {
        //uloga: '',
        korisnici: [],
        backup_korisnici: [],
        pretraga_kor_ime: "",
        pretraga_uloga: 5,
        pretraga_pol: 5,

        rezervacije: []
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
                //this.backup_korisnici = response.data; // !!! Ista referenca
                for (korisnik of this.korisnici) {
                    this.backup_korisnici.push(korisnik);
                }
            })
            .catch(error => {
                console.log(error);
                alert(error.response.data.sadrzaj);
                if (error.response.status == 400 || error.response.status == 403)   {
                    // Zakasni prelazak na login stranicu par sekundi da se poruka lepo vidi (ukoliko dodje do greske)
                    // UPDATE: Nema potrebe za kasnjenjem jer nece preci na login.html dokle god korisnik ne pritisne ok na alert poruci
                    //setTimeout(() => {
                        window.localStorage.removeItem('jwt');
                        window.location = 'login.html';
                    //}, 5000);
                }
            });

        // TODO: isti poziv za apartmane i mozda rezervacije
        axios
            .get('app/dobavi_rezervacije', {
                headers: {
                    'Authorization': 'Bearer ' + window.localStorage.getItem('jwt')
                }
            })
            .then(response => {
                this.rezervacije = response.data;
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
        obrisiKorisnika: function(korisnik) {
            if (confirm("Obriši korisnika " + korisnik.korisnickoIme + "?"))  {
                axios
                    .delete('app/obirsi_korisnika', {
                        headers: {
                            'Authorization': 'Bearer ' + window.localStorage.getItem('jwt')
                        },
                        data: korisnik
                    })
                    .then(response => {
                        this.korisnici = response.data;
                        //location.reload(); // nema potrebe za ovim, tabela se sama azurira
                    })
                    .catch(error => {
                        console.log(error);
                        alert(error.response.data.sadrzaj);
                        if (error.response.status == 400 || error.response.status == 403)   {
                            // Zakasni prelazak na login stranicu par sekundi da se poruka lepo vidi (ukoliko dodje do greske)
                            // UPDATE: Nema potrebe za kasnjenjem jer nece preci na login.html dokle god korisnik ne pritisne ok na alert poruci
                            //setTimeout(() => {
                                window.localStorage.removeItem('jwt');
                                window.location = 'login.html';
                            //}, 5000);
                        }
                    });
            }
        },

        filter: function()   {
            let vrednost = this.pretraga_kor_ime.toLowerCase();
            let filtrirani_korisnici = [];
            for (korisnik of this.backup_korisnici)    {
                // Ako korisnickoIme sadrzi string iz input polja && (ako se korisnikova uloga poklapa sa trazenom || uloga nije izabrana (dakle uzimaj sve uloge u obzir) && (isto kao za ulogu samo za pol))
                if (korisnik.korisnickoIme.toLowerCase().includes(vrednost) && (korisnik.uloga === this.pretraga_uloga || this.pretraga_uloga == 5) && (korisnik.pol === this.pretraga_pol || this.pretraga_pol == 5))    {
                    filtrirani_korisnici.push(korisnik);
                }
            }

            this.korisnici = filtrirani_korisnici;
        },

        prikaziStatus: function(status)   {
            switch (status) {
                case '0':
                    return 'Kreirana';
                case '1':
                    return 'Odbijena';
                case '2':
                    return 'Odustanak';
                case '3':
                    return 'Prihvaćena';
                case '4':
                    return 'Završena';
                default:
                    return 'Invalid status';
            }
        },

        prikaziPoruku: function(poruka) {
            // TODO: mozda dodati neki fiksan textbox ili dodatan red u tabeli u kom ce se prikazati poruka umesto alert
            alert(poruka);
        }
    }
});
