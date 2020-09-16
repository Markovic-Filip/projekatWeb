new Vue({
  el: '#dodaj_komentar-app',
  data: {
	  		apartman: {},
	  		tekst: '',
	        picked: '',
	        odobren: false,
	        valid: false
	    },
	    
	    mounted()   {
	    	this.apartman = JSON.parse(window.localStorage.getItem('apartman'));
	    },
	   
	    methods: {
	    	validacija: function() {
	            valid = true;
	            	/*
	            // provera combo box-eva
	            if (this.$refs.ocena.value == "") {
	                this.$refs.ocena.classList.remove("is-valid");
	                this.$refs.ocena.classList.add("is-invalid");
	                valid = false;
	            } else {
	                if (this.$refs.ocena.classList.contains('is-invalid'))    {
	                    this.$refs.ocena.classList.remove("is-invalid");
	                    this.$refs.ocena.classList.add("is-valid");
	                }
	            }*/
	            // proveri mesto
	            if (this.$refs.tekst.value.length <= 1 )   {
	                this.$refs.tekst.classList.remove("is-valid");
	                this.$refs.tekst.classList.add("is-invalid");
	                valid = false;
	            } else  {
	                if (this.$refs.tekst.classList.contains('is-invalid'))    {
	                	this.$refs.tekst.classList.remove("is-invalid");
	                	this.$refs.tekst.classList.add("is-valid");
	                }
	            }
       
	                if(valid) {
	                	this.dodajApartman();
	                }
	        
	    	},
	    	
	    	dodajApartman: function(){
	    		
	    		var komentar = {
	    				'apartman' : this.apartman.id,
	    				'tekst' : this.tekst,
	    				'ocena' : this.picked,
	    				'odobren' : this.odobren
	    				
	    				
	    		};
	    		
	    		let putanja = 'app/dodavanje_komentara';

	            axios
	                .post(putanja, komentar, {
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
	    		window.location = "gost_page.html";
	    		
	    		
	    		
	    		
	    	}
	    	

	    
	    
	    }
	    

	       
	});
