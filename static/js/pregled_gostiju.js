new Vue({
    el: '#pregled-gostiju-app',
    data:   {
    	korisnici: [],
    	pretraga: '',
    	pretraga_pol: '3'
       
    },
    mounted()  {
    	axios
        .get('app/dobavi_korisnike_domacin', {
            headers: {
                'Authorization': 'Bearer ' + window.localStorage.getItem('jwt')
            }
        })
        .then(response => {
            this.korisnici = response.data;
            
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
     
        
        
    },
    computed:{
    	filtriraniGosti: function(){
    		return this.korisnici.filter((korisnik) => {
    			

    

                return (korisnik.korisnickoIme.match(this.pretraga))
                    && (korisnik.pol == this.pretraga_pol || this.pretraga_pol == '3');

            });
        }
    }
});