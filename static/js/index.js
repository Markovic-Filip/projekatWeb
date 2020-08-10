new Vue({
    el: '#index-app',
    methods:    {
        ulogovan: function()    {
            let jwt = window.localStorage.getItem('jwt');
            if (jwt != null)    {
                return true;
            } else    {
                return false;
            }
        }
    }
});