new Vue({
    el: '#registration-app',
    data: {
        ime: 'Pera',
        prezime: 'Peric',
        korisnicko: 'pera52',
        lozinka: 'pera123',
        potvrdaLozinke: 'pera123',
        pol: '',
        uloga: ''
    },
    methods: {
        check: function()   {
            alert(this.ime + ' ' + this.korisnicko);
        }
    }
});