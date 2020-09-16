new Vue({
    el: '#gost-app',
    data:   {
    	rezervacije: [],
    	sortiranje: '',
    	apartmani: []
    },
    mounted()   {
    
        axios
            .get('app/dobavi_rezervacije_gost', {
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
        
        axios
        	.get('app/dobavi_apartmane_gost', {
        		headers: {
                    'Authorization': 'Bearer ' + window.localStorage.getItem('jwt')
        		}
        	})
        	.then(response => {
        		this.apartmani = response.data;
        	})
        	.catch(error => {
        		console.log(error);
        		alert(error.response.data.sadrzaj);
        	   
        	});
    
    

    },

    methods:    {
    	
    	promeniStatusRezervacije: function(rezervacija)    {
            
            
            	rezervacija.status = '2'
            	let datum = new Date(rezervacija.pocetniDatum)
            	rezervacija.pocetniDatum = datum.getTime()

            axios
                .put('app/promeni_status_rezervacije', rezervacija, {
                    headers:    {
                        'Content-Type': 'application/json',
                        'Authorization': 'Bearer ' + window.localStorage.getItem('jwt')
                    }
                })
                .then(response => {
                    this.$refs.msg.classList.remove("error-msg");
                    this.$refs.msg.innerHTML = response.data.sadrzaj;
                })
                .catch(error => {
                    console.log(error);
                    this.$refs.msg.classList.add("error-msg");
                    this.$refs.msg.innerHTML = error.response.data.sadrzaj;
                    if (error.response.status == 400 || error.response.status == 403)   {
                        window.localStorage.removeItem('apartman');
                        window.localStorage.removeItem('jwt');
                        window.location = 'login.html';
                    }
                });
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
        
        prikaziAdresu: function(apartman)   {
            return apartman.lokacija.adresa['ulica'] + " " + apartman.lokacija.adresa['broj'] + "\n" + apartman.lokacija.adresa['mesto'] + "\n" + apartman.lokacija.adresa['postanskiBroj'];
        },
     
        prikaziPoruku: function(poruka) {
            this.$refs.poruka.innerText = "Poruka: \n" + poruka;
        },
        dodajKomentar: function(apartman)   {
            window.localStorage.setItem('apartman', JSON.stringify(apartman));
            window.location = 'http://localhost:8080/dodaj_komentar.html';
        },
        rastuce: function (a, b) {
            if ( a.cena < b.cena )  {
              return -1;
            }
            if ( a.cena > b.cena )  {
              return 1;
            }
            return 0;
        },

        opadajuce: function(a, b)   {
            if ( a.cena > b.cena )  {
                return -1;
              }
              if ( a.cena < b.cena )  {
                return 1;
              }
              return 0;
        },

        sortirajPoCeni: function(event)  {
            if (this.sortiranje == 'rastuce')   {
                this.rezervacije.sort(this.rastuce);
            } else  {
                this.rezervacije.sort(this.opadajuce);
            }
        }
    

    }
});
