function getParameterByName(name, url) {
	if (!url){
		url = window.location.href;
	}
	name = name.replace(/[\[\]]/g, "\\$&");
	var regex = new RegExp("[?&]" + name + "(=([^&#]*)|&|#|$)"), results = regex.exec(url);
	if (!results) return null;
	if (!results[2]) return '';
	return decodeURIComponent(results[2].replace(/\+/g, " "));
}

function preparePlayer() {
	var url = window.location.href;
	var parametersString = url.substring(url.indexOf("?") + 1);
	var parameters = parametersString.split("&");
	var params = {};
	for(key in parameters) {
		var tmp = parameters[key].split("=");
		params[tmp[0]] = tmp[1];
	}

	var typeIndex = parseInt(params["type"]);
	var url = params["url"];

	document.getElementById("video_player").innerHTML = "<source src=\"" + params["url"] + "\" />";

	if(typeIndex == 0) {
		var context = Dash.di.DashContext();
		var player = new MediaPlayer(context);
		player.startup();
		player.attachView(document.querySelector("#video_player"));
		player.attachSource(url);
	}
}
