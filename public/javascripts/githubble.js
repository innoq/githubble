var h=1080,
	w=1920,
	r=50;
var radius = {
	"user" : 32,
	"orga" : 64
};
var force = d3.layout.force()
    .charge(-1000)
    .linkDistance(function(link) {
			return link.class === "owns" ? 
				250 : 
				link.class === "member" ? 
					400 : 
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
var user = "";
var type = "user";

function setup(){
	document.getElementById("ui.value").value = user;
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
    addEvent(document.getElementById("ui.submit"), 'click', function(){
    	var parts = window.location.href.split("?");
    	var value = document.getElementById("ui.value").value;
    	typeSelect = document.getElementById("ui.type");
		var type = typeSelect.options[typeSelect.selectedIndex].value;
		console.log("calling "+type+ " "+value);
    	window.location.href = parts[0]+"?"+type+"&"+value;
    });	        	
}

function detectUrl(){
	var parts = window.location.href.split("?");
	if(parts.length > 1){
		parts = parts[1].split("&");
		if(parts.length > 1){
			user = parts[1];
			type = parts[0];
		}
	}
}

function update(){
	var path = "/users/"+user;
	//var path = "test.json"
	console.log("call backend with "+path);
	d3.json(path, function(error, graph) {
		console.log("nodes count "+graph.nodes.length);
	  	console.log("links count "+graph.links.length);
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
function click(d) {}	