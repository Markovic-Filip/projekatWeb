new Vue({
    el: '#add_apartment-app',
    data: {
        tip: -1,
        brojSoba: 1,
        brojGostiju: '',
        ulica: '',
        broj: '',
        mesto: '',
        drzava: '',
        postanskiBroj: '',
        geografskaSirina: '',
        geografskaDuzina: '',
        korisnickoImeDomacina: '',
        cenaPoNoci: '',
        vremeZaPrijavu: '',
        vremeZaOdjavu: '',
        status: -1,
        idSadrzaja: [],
        idRezervacije: [],
        sadrzaj: [],
        
        
        valid: true
    },
    mounted()   {
      
        axios
            .get('app/dobavi_sadrzaj_apartmana', {
                headers: {
                    'Authorization': 'Bearer ' + window.localStorage.getItem('jwt')
                }
            })
            .then(response => {
                this.sadrzaj = response.data;
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
    methods: {
    	validacija: function() {
            valid = true;

            // provera combo box-eva
            if (this.$refs.tip.value == "") {
                this.$refs.tip.classList.remove("is-valid");
                this.$refs.tip.classList.add("is-invalid");
                valid = false;
            } else {
                if (this.$refs.tip.classList.contains('is-invalid'))    {
                    this.$refs.tip.classList.remove("is-invalid");
                    this.$refs.tip.classList.add("is-valid");
                }
        
        // proveri broj soba
        if (this.$refs.brojSoba.value.length < 1 || !/^[0-9]+$/.test(this.$refs.brojSoba.value))   {
            this.$refs.brojSoba.classList.remove("is-valid");
            this.$refs.brojSoba.classList.add("is-invalid");
            valid = false;
        } else  {
            if (this.$refs.brojSoba.classList.contains('is-invalid'))    {
                this.$refs.brojSoba.classList.remove("is-invalid");
                this.$refs.brojSoba.classList.add("is-valid");
            }
        }
        
     // proveri broj gostiju
        if (this.$refs.brojGostiju.value.length < 1 || !/^[0-9]+$/.test(this.$refs.brojGostiju.value))   {
            this.$refs.brojGostiju.classList.remove("is-valid");
            this.$refs.brojGostiju.classList.add("is-invalid");
            valid = false;
        } else  {
            if (this.$refs.brojGostiju.classList.contains('is-invalid'))    {
                this.$refs.brojGostiju.classList.remove("is-invalid");
                this.$refs.brojGostiju.classList.add("is-valid");
            }
        }
        
     // proveri ulicu
        if (this.$refs.ulica.value.length <= 1 || !/^[a-zA-Z0-9 ]+$/.test(this.$refs.ulica.value))   {
            this.$refs.ulica.classList.remove("is-valid");
            this.$refs.ulica.classList.add("is-invalid");
            valid = false;
        } else  {
            if (this.$refs.ulica.classList.contains('is-invalid'))    {
                this.$refs.ulica.classList.remove("is-invalid");
                this.$refs.ulica.classList.add("is-valid");
            }
        }
        
     // proveri broj ulice
        if (this.$refs.broj.value.length < 1 || !/^[0-9]+$/.test(this.$refs.broj.value))   {
            this.$refs.broj.classList.remove("is-valid");
            this.$refs.broj.classList.add("is-invalid");
            valid = false;
        } else  {
            if (this.$refs.broj.classList.contains('is-invalid'))    {
                this.$refs.broj.classList.remove("is-invalid");
                this.$refs.broj.classList.add("is-valid");
            }
        }
        
     // proveri mesto
        if (this.$refs.mesto.value.length <= 1 || !/^[a-zA-Z ]+$/.test(this.$refs.mesto.value))   {
            this.$refs.mesto.classList.remove("is-valid");
            this.$refs.mesto.classList.add("is-invalid");
            valid = false;
        } else  {
            if (this.$refs.mesto.classList.contains('is-invalid'))    {
                this.$refs.mesto.classList.remove("is-invalid");
                this.$refs.mesto.classList.add("is-valid");
            }
        }
        
        // proveri drzavu
        if (this.$refs.drzava.value.length <= 1 || !/^[a-zA-Z ]+$/.test(this.$refs.drzava.value))   {
            this.$refs.drzava.classList.remove("is-valid");
            this.$refs.drzava.classList.add("is-invalid");
            valid = false;
        } else  {
            if (this.$refs.drzava.classList.contains('is-invalid'))    {
                this.$refs.drzava.classList.remove("is-invalid");
                this.$refs.drzava.classList.add("is-valid");
            }
        }
        
     // proveri postanski broj
        if (this.$refs.postanskiBroj.value.length < 4 || this.$refs.postanskiBroj.value.length > 7 || !/^[0-9]+$/.test(this.$refs.postanskiBroj.value))   {
            this.$refs.postanskiBroj.classList.remove("is-valid");
            this.$refs.postanskiBroj.classList.add("is-invalid");
            valid = false;
        } else  {
            if (this.$refs.postanskiBroj.classList.contains('is-invalid'))    {
                this.$refs.postanskiBroj.classList.remove("is-invalid");
                this.$refs.postanskiBroj.classList.add("is-valid");
            }
        }
        
     // proveri geografskaSirina
        if (this.$refs.geografskaSirina.value.length < 1 || !/^[0-9]+\.[0-9]+$/.test(this.$refs.geografskaSirina.value))   {
            this.$refs.geografskaSirina.classList.remove("is-valid");
            this.$refs.geografskaSirina.classList.add("is-invalid");
            valid = false;
        } else  {
            if (this.$refs.geografskaSirina.classList.contains('is-invalid'))    {
                this.$refs.geografskaSirina.classList.remove("is-invalid");
                this.$refs.geografskaSirina.classList.add("is-valid");
            }
        }
        
        // proveri geografskaDuzina
        if (this.$refs.geografskaDuzina.value.length < 1 || !/^[0-9]+\.[0-9]+$/.test(this.$refs.geografskaDuzina.value))   {
            this.$refs.geografskaDuzina.classList.remove("is-valid");
            this.$refs.geografskaDuzina.classList.add("is-invalid");
            valid = false;
        } else  {
            if (this.$refs.geografskaDuzina.classList.contains('is-invalid'))    {
                this.$refs.geografskaDuzina.classList.remove("is-invalid");
                this.$refs.geografskaDuzina.classList.add("is-valid");
            }
        }
        
     // proveri cena
        if (this.$refs.cenaPoNoci.value.length < 1 || !/^[0-9]+\.[0-9]+$/.test(this.$refs.cenaPoNoci.value))   {
            this.$refs.cenaPoNoci.classList.remove("is-valid");
            this.$refs.cenaPoNoci.classList.add("is-invalid");
            valid = false;
        } else  {
            if (this.$refs.cenaPoNoci.classList.contains('is-invalid'))    {
                this.$refs.cenaPoNoci.classList.remove("is-invalid");
                this.$refs.cenaPoNoci.classList.add("is-valid");
            }
        }
        
    	
        
        if(valid) {
        	this.dodajApartman();
        }
        
    	},
    	
    	dodajApartman: function(){
    		
    		var adresa = {
    				'ulica' : this.ulica,
    				'broj' : this.broj,
    				'mesto' : this.mesto + ' - ' + this.drzava,
    				'postanskiBroj' : this.postanskiBroj
    		};
    		
    		var lokacija = {
    				'geografskaSirina' : this.geografskaSirina,
    				'geografskaDuzina' : this.geografskaDuzina,
    				'adresa' : adresa
    		};
    		
    		var apartman = {
    				'tip' : this.tip,
    				'brojSoba' : this.brojSoba,
    				'brojGostiju' : this.brojGostiju,
    				'lokacija' : lokacija,
    				'cenaPoNoci' : this.cenaPoNoci,
    				'idSadrzaja' : this.idSadrzaja
    				
    		};
    		
    		let putanja = 'app/dodavanje_apartmana';

            axios
                .post(putanja, apartman, {
                    headers: {
                        'Content-Type': 'application/json',
                        'Authorization': 'Bearer ' + window.localStorage.getItem('jwt')
                    }
                })
                .then(response => {
                    this.$refs.msg.classList.remove("error-msg");
                    this.$refs.msg.classList.add("ok-msg");
                    this.$refs.msg.innerHTML = response.data.sadrzaj;
                })
                .catch(error => {
                    console.log(error);
                    //alert(error.response.data.sadrzaj);
                    this.$refs.msg.classList.add("error-msg");
                    this.$refs.msg.innerHTML = error.response.data.sadrzaj;
                });
    		
    		
    		
    		
    		
    	}

    
    
    }
    

       
});
