$(function(){
	$('#demo-nav').find('a').tooltip({effect: 'slide'});
});

$(window).load(function(){
	$('#slider').ccslider({
		_3dOptions: {
			imageWidth: 1093,
			imageHeight: 402
		}
	});
	$('#left-banner').ccslider({
		effectType: '2d',
		effect: 'random'
	});
});
