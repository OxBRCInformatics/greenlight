/**
 * Created by PD on 2/4/14.
 */

//This method fixes the JQuery Form Validator and Bootstrap compatibility
//FROM http://jsfiddle.net/mapb_1990/hTPY7/7/
(function()
{
    $.validator.setDefaults({
        highlight: function(element) {
            $(element).closest('.form-group').addClass('has-error');
        },
        unhighlight: function(element) {
            $(element).closest('.form-group').removeClass('has-error');
        },
        errorElement: 'span',
        errorClass: 'help-block',
        errorPlacement: function(error, element) {
            if(element.parent('.input-group').length) {
                error.insertAfter(element.parent());
            } else {
                error.insertAfter(element);
            }
        }
    });
})()



