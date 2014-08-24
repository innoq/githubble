var resolution = getResultion();
var h=resolution.h; // 720
	w=resolution.w; //1280,
	r=50;
var radius = {
	"user" : 32,
	"orga" : 64
};
var force = d3.layout.force()
    .charge(-1000)
    .linkDistance(function(link) {
			return link.class === "owns" ? 
				300 : 
				link.class === "member" ? 
					150 : 
					500;
	})
    .gravity(0.05)
    .size([w, h]);	    
var svg = d3.select("body").append("svg")
    .attr("width", w)
    .attr("height", h);
svg.append("defs").attr("id", "defs");
var pattern = d3.select("defs").selectAll(".pattern");
var types = ["user", "repo", "orga"];
var value = "";
var type = "user";

function setup(){
	document.getElementById("ui.value").value = value;
	var typeSelect = document.getElementById("ui.type");
	var i = 0;
	types.forEach(function(entry) {
		var option = document.createElement("option");
		option.text = entry;
		option.value = entry;
		typeSelect.appendChild(option);
		if(entry === type){
			typeSelect[i].selected = true;
		}
		i++;
		
	});

    addEvent(document.getElementById("ui.value"), 'keydown', function(e) {
        if (e.which == 13) {
           changeContextByUi();
        }
    });

    addEvent(document.getElementById("ui.submit"), 'click', function(){
    	changeContextByUi();
    });	        	
}

function changeContextByUi(){
    var parts = window.location.href.split("?");
        var value = document.getElementById("ui.value").value;
        typeSelect = document.getElementById("ui.type");
        var type = typeSelect.options[typeSelect.selectedIndex].value;
        console.log("calling "+type+ " "+value);
        window.location.href = parts[0]+"?"+type+"="+value;
}

function detectUrl(){
	var parts = window.location.href.split("?");
	if(parts.length > 1){
		parts = parts[1].split("=");
		if(parts.length > 1){
			value = parts[1];
			type = parts[0];
		}
	}
}

function handleError(error) {
    var body = document.getElementsByTagName("body")[0];
    var warn = document.createElement("div");
    document.getElementById("status").hidden = true;
    document.getElementById("query").hidden = true;
    document.getElementById("legend").hidden = true;
    document.getElementById("history").hidden = true;
    
    console.log(error.responseText);
    // TODO make this nicer and/or more secure?
    var info = eval('('+error.responseText+')').status;
    var time = makeTime(info.reset);
    warn.innerHTML = "<h2>Error performing your Request!</h2><br />All "+info.limit+" Calls were made.<br />please try again later!<br />(around "+time.full+")";
    warn.setAttribute("class", "ui");
    warn.setAttribute("id", "warn");
    body.appendChild(warn);
    return console.warn(error);
}

function updateStatus(status){
    var statusUI = document.getElementById("status");
   	// TODO do this in a proper way, innerHTML is EVIL.
   	if(status) {
    	var time = makeTime(status.reset);
    	statusUI.innerHTML = status.remaining+" Calls remaining<br />until "+time.full;   	
   	}
   	statusUI.style.display = "block";
}

function updateHistory(history){
	var historyUI = document.getElementById("history");
	var out = document.createElement("ul");
	for (var i in history) {
		var li = document.createElement("li");
		li.setAttribute("class", "historyLink");
    	addEvent(li, 'click', function(){
    		var kv = history[i].split("/");
    		var parts = window.location.href.split("?");
    		window.location.href = parts[0]+"?"+kv[0]+"="+kv[1];    		
    	});		
		out.appendChild(li);
	}
	historyUI.appendChild(out);
	historyUI.style.display = "block";
}

function updateLegend(){
	var legendUI = document.getElementById("legend");
	// TODO do this in a proper way, innerHTML is EVIL.
	if(type === "user"){
		legendUI.innerHTML = " (((( user ) orgas ) repos ) followers )";
	}
	if(type === "orga"){
		legendUI.innerHTML = " (((  orga ) repos ) members )";
	}	
	if(type === "repo"){
		legendUI.innerHTML = " ((( repo ) forks ) contributors )";
	}	
	legendUI.style.display = "block";	
}

function update(){
	//var path = "/assets/test.json"
	var path = "";
	if(type === "repo"){
		path = reposControllerPath.replace(":repo", encodeURIComponent(value));
	} else if(type === "user"){
		path = usersControllerPath.replace(":user", encodeURIComponent(value));		
	} else {
		path = orgasControllerPath.replace(":orga", encodeURIComponent(value));	
	}

	console.log("call backend with "+path);
	d3.json(path, function(error, graph) {
        if (error) {
            return handleError(error);
        } 
		console.log("nodes count "+graph.nodes.length);
	  	console.log("links count "+graph.links.length);
        updateStatus(graph.status);
		
		d3.json(historyControllerPath, function(error, history) {
			updateHistory(history);
		});
		
		updateLegend();
	  	force
	      .nodes(graph.nodes)
	      .links(graph.links)
	      .start();	
		updatePattern(graph.nodes);
		var link = svg.selectAll(".link")
			.data(graph.links)
	    	.enter().append("line")
	      	.attr("class", "link")
	      	.style("stroke-width", function(d) { return Math.sqrt(d.value); });
    	var node = svg.selectAll(".node")
    		.data(graph.nodes);

    	var nodeEnter = node.enter().append("g")
            .attr("id", function(d) { return d.id; })
            .attr("class", function(d) { return "node "+d.class; })
            .on("click", changeContext)
            .call(force.drag);
        
        nodeEnter.filter(function(d){ return d.class === "orga" || d.class === "user" })
            .append("circle")
	      	.attr("r",  function(d) { return getR(d)+6; })
	      	.attr("fill", "#fff")
	      	.attr("cx", function(d){ return d.class === "orga" ? 5 : 2})
			.attr("cy", function(d){ return d.class === "orga" ? 5 : 2});

	    nodeEnter.filter(function(d){ return d.class === "orga" || d.class === "user" })  	
        	.append("circle")
	      	.attr("r",  function(d) { return getR(d)+4; })
	      	.attr("fill", "#fff");

        nodeEnter.append("circle")
	      	.attr("r",  function(d) {return getR(d);})
			.attr("transform", function(d) {
                return "translate(-" + getR(d) +" , -" + getR(d) + ")";
            })
            .attr("class", "avatar")
            .attr("cx", function(d) {return getR(d)})	
            .attr("cy", function(d) {return getR(d)})			      	
	      	.attr("fill", function(d) {
                if (d.class === "user" || d.class === "orga") {
                    return "url(#images-" + d.id + ")";
                } else {
                    return  color(d);
                }
            });
	    
		nodeEnter.append("text")
            .attr("dy", ".35em")
            .text(function(d) {
                return d.label;
            }).attr("transform", function(d) {
            	return d.class === "user" ? 
            		"translate("+getR(d)*2 +", " + getR(d)*0.7 + ")" :
            		d.class === "orga" ?
            			"translate(0 , " + getR(d)*1.5 + ")" :
            			"translate(75 , 0)";
            });

		force.on("tick", function() {
	    	link.attr("x1", function(d) { return boxX(d.source.x); })
	        	.attr("y1", function(d) { return boxY(d.source.y); })
	        	.attr("x2", function(d) { return boxX(d.target.x); })
	        	.attr("y2", function(d) { return boxY(d.target.y); });
    		node.attr("transform", function(d) {
    			return "translate(" + boxX(d.x) + "," + boxY(d.y) + ")";
    		});
	  	});	
	});
}

function updatePattern(nodes) {
    pattern = pattern.data(nodes.filter(function(d) {
        return d.class === "user" || d.class === "orga";
    }));

    pattern
        .exit()
        .remove();

    pattern
        .enter()
        .append("pattern")
        .attr("class", "pattern")
        .attr("id", function(d) {
            if (d.class === "user" || d.class === "orga") {
                return "images-" + d.id;
            } else {
                return "";
            }
        })
        .attr("x", "0").attr("y", "0")
        .attr("patternUnits", "userSpaceOnUse")
        .attr("width", function(d){return d.class === "user" ? radius.user * 2 : radius.orga * 2})
        .attr("height",function(d){return d.class === "user" ? radius.user * 2 : radius.orga * 2})
        .append("image")
        .attr("x", "0").attr("y", "0")
        .attr("width", function(d){return d.class === "user" ? radius.user * 2 : radius.orga * 2})
        .attr("height", function(d){return d.class === "user" ? radius.user * 2 : radius.orga * 2})
        .attr("xlink:href", function(d) {
            if (d.class === "user" || d.class === "orga") {
                return d.avatar;
            } else {
                return "";
            }
        });
}

// change Context onClick
var changeContext = function(d) {
    if (d3.event.defaultPrevented) return;
    var parts = window.location.href.split("?");
    var type = d.class.replace("node ", "");
    window.location.href = parts[0]+"?"+type+"="+d.label;
}



function getResultion(){
    var winW = 1280;
    var winH = 720;
    if (document.body && document.body.offsetWidth) {
        winW = document.body.offsetWidth;
        winH = document.body.offsetHeight;
    }
    if (document.compatMode=='CSS1Compat' &&
        document.documentElement &&
        document.documentElement.offsetWidth ) {
        winW = document.documentElement.offsetWidth;
        winH = document.documentElement.offsetHeight;
    }
    if (window.innerWidth && window.innerHeight) {
        winW = window.innerWidth;
        winH = window.innerHeight;
    }
    return {"w":winW, "h":winH};
}

function addEvent(element, evnt, funct){
  if (element.attachEvent)
   return element.attachEvent('on'+evnt, funct);
  else
   return element.addEventListener(evnt, funct, false);
}

function getR(d){
	return d.class === "user" ? 
    	radius.user: 
    	d.class === "orga" ? 
    		radius.orga : 10;	
}

// prevent node and links moving out of viewing area.
function boxX(x){
	return Math.max(r, Math.min(w - r, x));
}

function boxY(y){
	return Math.max(r, Math.min(h - r, y));
}

function color(d) {
	return d.class === "user" ? "#3182bd" // collapsed package
        : d.class === "repo" ? "#c6dbef" // expanded package
        : "#fd8d3c"; // leaf node
}

// found here http://blog.kim-maida.com/web-development/unix-timestamp-to-js-local
function makeTime(timestamp){
    var d = new Date(timestamp * 1000), // Convert to milliseconds
        yyyy = d.getFullYear(),
        mm = ('0' + (d.getMonth() + 1)).slice(-2),  // Months are zero based. Add leading 0.
        dd = ('0' + d.getDate()).slice(-2),         // Add leading 0.
        hh = d.getHours(),
        min = ('0' + d.getMinutes()).slice(-2),     // Add leading 0.
        time;
        
    return {"full" : dd + '.' + mm + '.' + yyyy + ', ' + hh + ':' + min };
}