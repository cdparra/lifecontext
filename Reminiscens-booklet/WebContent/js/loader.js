        		$(document).ready(function() {
        $('#myModal').reveal();
        TT.initialize();

        $('#getData').click(function(){

        var decade = $("#decade").val();
        var place = $('#place').val();

        $('#form').remove();
        $('#myModal').append('<img id="spinner" src="http://www.antonioeantoniocaserta.it/images/spinner.gif"/>');

        geocode(place, function(loc) {

        if (loc != null) {

        $.ajax({
                            url:'booklet?decade='+decade+'&lat='+loc.lat()+'&lon='+loc.lng(),
                            type:'post',
                            dataType: 'json',
                            success: function(data) {
        var mediaData = data[0];
        var eventsData = data[1];

        $('#myModal').trigger('reveal:close');
        TT.paperstack.initialize();

        TT.pageflip.initialize();

        $('.arrow').show();
                            }
                        });
        }
        });

                   

                    });
        
		Shadowbox.init({
			enableKeys : false
		});
        });