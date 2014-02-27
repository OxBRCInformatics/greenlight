/**
 * Created by Soheil on 2/27/14.
 */
/*
 Magnifying Glass for Images v1.1
 By Zaus (http://codepen.io/zaus)
 http://jsfiddle.net/ZVbmp/
 */

// scope protection
var Magnifier = (function($){
    var LIB = {
        /* standard selection */
        selectors : {
            magnify : '.magnify'
            , glass_class : 'glass' // lame
            , glass : '.glass'
            , thumb : '.thumb'
            , active_class : 'active'
        }
        ,
        /* remember dom elements */
        $el : { }
        ,
        /* prepare all stuff */
        init : function(){
            // get dom elements
            LIB.$el.magnifiers = $(LIB.selectors.magnify);

            //add glass to each magnifier
            var $glass = $('<div></div>').addClass(LIB.selectors.glass_class);

            // get the native image size of each magnifier source image
            LIB.$el.magnifiers.each(function(i,o){
                var $magnifier = $(o);
                var $thumb = $magnifier.find(LIB.selectors.thumb);


                // use Image object to get the dimensions
                var image_object = new Image();
                image_object.src = $thumb.attr("src");


                // save for later
                $magnifier.data({"native_w":image_object.width, "native_h":image_object.height});

                // attach behaviors
                $magnifier.mousemove(LIB.behaviors.mousemove);

                // add glass
                $thumb.before( $glass.clone().css('background-image', 'url(' + $thumb.attr('src') + ')') );
            });

        }//--        fn        init
        ,
        behaviors : {
            /* delay for...fade */
            fadeDelay : 300
            ,
            /* fade in/out glass overlay if mouse is outside container */
            isHover : function(cw, ch, mx, my){
                return (mx < cw && my < ch && mx > 0 && my > 0);
            }//--        fn hover
            ,
            /* move glass overlay */
            mousemove : function(e){
                var $magnifier = $(this)
                    , offset = $magnifier.offset() // relative position
                    , mx = e.pageX - offset.left // relative to mouse
                    , my = e.pageY - offset.top // relative to mouse
                    , $glass = $magnifier.find(LIB.selectors.glass)
                    , $thumb = $magnifier.find(LIB.selectors.thumb)
                    , rx, ry, bgp // relative ratios
                    , native_width = $magnifier.data('native_w')
                    , native_height = $magnifier.data('native_h')
                    , glass_width = $glass.width()
                    , glass_height = $glass.height()
                    ;


                if( LIB.behaviors.isHover($magnifier.width(), $magnifier.height(), mx, my) ) {
                    // show
                    $glass.fadeIn(LIB.behaviors.fadeDelay);

                    //The background position of .glass will be changed according to the position
                    //of the mouse over the .small image. So we will get the ratio of the pixel
                    //under the mouse pointer with respect to the image and use that to position the
                    //large image inside the magnifying glass
                    rx = Math.round(mx/$thumb.width()*native_width - glass_width/2)*-1;
                    ry = Math.round(my/$thumb.height()*native_height - glass_height/2)*-1;
                    bgp = rx + "px " + ry + "px";

                    //The logic is to deduct half of the glass's width and height from the
                    //mouse coordinates to place it with its center at the mouse coordinates
                    $glass.css({
                        left: mx - glass_width/2
                        , top: my - glass_height/2
                        , backgroundPosition: bgp
                    });

                }//-- if visible
                else {
                    // hide
                    $glass.fadeOut(LIB.behaviors.fadeDelay);
                }//-- if !visible
            }//--        fn mousemove
        }//--        behaviors
        ,
        /* include execution in page, with .ready wrapper */
        ready : function(){
            // ready
            $(function(){
                // setup
                LIB.init();
            });
        }//--    fn    ready

    };////----        LIB
    return LIB;
})($);

// engage
Magnifier.ready();