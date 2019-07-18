<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html>

<head>
	<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
	<title>EVEMap</title>
	<style>
body {
	margin: 0px;
	padding: 10px 0 0 0;
	font-family: verdana, arial, helvetica, sans-serif;
	font-size: 14px;
	color: white;
	background-color: #00037D;
	text-align: center;
}
a {
	text-decoration: none;
	color: white;
	border: 1px dotted white;
	border-width: 0 0 1px 0;
}
a:hover {
	border: 1px solid white;
	border-width: 0 0 1px 0;
	color: red;
}
h1 { 
	font-size: 32px;
	font-weight: bold;
	line-height: 20px;
	display: inline;
}
h2 {
	margin: 16px 0 8px 0;
	padding: 5px 0;
	font-size: 20px;
	font-weight: bold;
	line-height: 16px;
	text-decoration: underline;
	font-weight: normal;
}
p {
	margin: 5px 10px;
	padding: 0;
}
.container {
	width: 980px;
	margin-left: auto;
	margin-right: auto;
	line-height: 1.25
}
.title {
	padding: 10px;
	border: 1px solid white;
	border-width: 1px 1px 0 1px;
}
.menu {
	padding: 10px;
	border: 1px solid white;
}
.menu ul li {
	display: inline;
	margin-right: 0.5em;
	font-size: 14px;
}
.menu a {
	font-weight: bold;
}
.main {
	float: left;
	width: 958px;
	padding: 10px;
	border: 1px solid white;
	border-width: 0 1px 1px 1px;
	text-align: left;
	margin-bottom: 5px;
}
.important {
	font-size: 20px;
	margin: 20px;
}
.important a {
	border: 1px solid red;
	padding: 5px;
}
.changelog {
	display: none;
}
.changelog-current {
	display: block;
}
	</style>
	<script type="text/javascript">
	<!--
	function toggleCL(id) {
		if (document.getElementById(id).style.display == 'block') {
			document.getElementById(id).style.display = 'none';
		} else {
			document.getElementById(id).style.display = 'block';
		}
	}
	-->
	</script>
</head>

<body>

<div class="container">
	<div class="title">
		<h1>EVEMap</h1>
	</div>
	<div class="menu">
		<ul>
			<li><a href="#currentversion">Current version</a></li>
			<li><a href="#run">Run</a></li>
			<li><a href="#description">Description</a></li>
			<li><a href="#requirements">Requirements</a></li>
			<li><a href="#feedback">Feedback</a></li>
			<li><a href="#author">Author</a></li>
			<li><a href="#credits">Credits</a></li>
			<li><a href="#donations">Donations</a></li>
			<li><a href="#changelog">Changelog</a></li>
			<li><a href="#screenshots">Screenshots</a></li>
		</ul>
	</div>
	<div class="main">

		<div id="currentversion">
			<h2>Current version:</h2>
			<p><strong>1.4</strong></p>
		</div>
		
		<div id="run">
			<h2>Run:</h2>
			<p>Running the application is as simple as clicking the webstart link below:</p>
			<p class="important"><a href="evemap.jnlp">Run webstart</a></p>
		</div>
		
		<div id="description">
			<h2>Description:</h2>
			<p>EVEMap is an out of game map of the EVE universe. It shows all solarsystems and the connections between them. A lot of different display options and filters can be applied to get a good overview of the current status of the universe.</p>
			<p>It is also possible to plan routes using jumpgates and jumpdrives for ships that can use them. For the routeplanning there are also options like security status available.</p>
		</div>
		
		<div id="requirements">
			<h2>Requirements:</h2>
			<ul>
				<li>Java Runtime Environment version 1.5 or higher.</li>
				<li>A graphics card capable of doing OpenGL.</li>
			</ul>
			<p>All computers that can run EVE should be able to run EVEMap, and some that don't should run it as well.</p>
		</div>
		
		<div id="feedback">
			<h2>Feedback:</h2>
			<p>For feature requests, bugs, notes, appreciations, etc. please reply to the 
			<a href="http://myeve.eve-online.com/ingameboard.asp?a=topic&threadID=764169">EVE-Online forum thread</a>.</p>
		</div>
		
		<div id="author">
			<h2>Author:</h2>
			<p>Paul van Santen aka 'AcriQuo'</p>
		</div>
		
		<div id="credits">
			<h2>Credits:</h2>
			<p>I would like to thank the following people for testing the program:</p>
			<ul>
				<li>Klassac</li>
				<li>Ocularis</li>
				<li>Slothook</li>
				<li>Mr Krosis</li>
			</ul>
		</div>
		
		<div id="donations">
			<h2>Donations:</h2>
			<p>Feel free to send any isk to 'AcriQuo' :)</p>
		</div>

		<div id="changelog">
			<h2>Changelog:</h2></a>
			<ul>
				<li><strong>Version 1.4</strong> (<a href="#v1.4" onclick="toggleCL('v1.4'); return false;">Toggle</a>)
					<ul class="changelog-current" id="v1.4">
						
						<li>Added anomalies filter for wormhole space</li>
						<li>POS jumpbridge planner now avoids empire space and faction 0.0</li>
						<li>Improved keyboard navigation</li>
						<li>Other minor improvements</li>
						<li>Lots of code refactoring</li>
					</ul>
				</li>
				<li><strong>Version 1.3</strong> (<a href="#v1.3" onclick="toggleCL('v1.3'); return false;">Toggle</a>)
					<ul class="changelog" id="v1.3">
						<li>Updated mapdata to latest datadump</li>
						<li>Added view option for unknown wormhole space</li>
						<li>Added class filter (especially useful in wormhole space)</li>
						<li>Added POS jumpbridge plan functionality</li>
						<li>Other minor improvements</li>
						<li>Lots of code refactoring</li>
					</ul>
				</li>
				<li><strong>Version 1.2</strong> (<a href="#v1.2" onclick="toggleCL('v1.2'); return false;">Toggle</a>)
					<ul class="changelog" id="v1.2">
						<li>Updated mapdata to latest datadump</li>
						<li>The following stargate routes have been removed:
							<ul>
								<li>Komo <-> Motsu</li>
								<li>Komo <-> Muvolailen</li>
								<li>Saila <-> Oichiya</li>
								<li>Saila <-> Motsu</li>
								<li>Saila <-> Laah</li>
							</ul>
						</li>
						<li>The following stargate routes have been added:
							<ul>
								<li>Oichiya <-> Laah</li>
								<li>Komo <-> Oichiya</li>
							</ul>
						</li>
						<li>The following stations belonging to the Caldari Navy have been deployed: 
							<ul>
								<li>Ichoriya V Caldari Navy Logistic Support</li>
								<li>Tintoh VIII Caldari Navy Testing Facilities</li>
							</ul>
						</li>
					</ul>
				</li>
				<li><strong>Version 1.1</strong> (<a href="#v1.1" onclick="toggleCL('v1.1'); return false;">Toggle</a>)
					<ul class="changelog" id="v1.1">
						<li>Updated mapdata to latest datadump</li>
						<li>Added 3 missing jumps around Jita</li>
						<li>Renamed program 'EveMap' to 'EVEMap'</li>
						<li>Fixed map auto rotation</li>
					</ul>
				</li>
				<li><strong>Version 1.05</strong> (<a href="#v1.05" onclick="toggleCL('v1.05'); return false;">Toggle</a>)
					<ul class="changelog" id="v1.05">
						<li>Updated mapdata to latest official CCP Empyrean Age dump</li>
						<li>Reintroduced map flattening</li>
						<li>Tweaked (reduced) blinky star size</li>
						<li>Dimmed the brightness of connection line colors a bit</li>
						<li>Possible bugfix for thick connection line bug</li>
					</ul>
				</li>
				<li><strong>Version 1.0</strong> (<a href="#v1.0" onclick="toggleCL('v1.0'); return false;">Toggle</a>)
					<ul class="changelog" id="v1.0">
						<li>Added Black Rise region</li>
						<li>Added occupancy display filter</li>
					</ul>
				</li>
				<li><strong>Version 0.9</strong> (<a href="#v0.9" onclick="toggleCL('v0.9'); return false;">Toggle</a>)
					<ul class="changelog" id="v0.9">
						<li>New display option: Show 0.0 true security status</li>
						<li>Now highlights solarsystems in jumprange of hovered solarsystem</li>
						<li>Application stops attempting to download API data after 2 failures</li>
						<li>Stopped displaying solarsystems without faction when faction filter is on</li>
						<li>Stopped displaying solarsystems without sovereignty when sovereignty filter is on</li>
						<li>Enlarged solarsystem name in hover info overlay</li>
						<li>Improved rendering performance (again)</li>
						<li>Bugfix: Error when trying to load API data for nonexistant systems (Black Rise..)</li>
					</ul>
				</li>
				<li><strong>Version 0.85</strong> (<a href="#v0.85" onclick="toggleCL('v0.85'); return false;">Toggle</a>)
					<ul class="changelog" id="v0.85">
						<li>Improved rendering performance</li>
						<li>Tweaked the stars to be slightly smaller</li>
					</ul>
				</li>
				<li><strong>Version 0.8</strong> (<a href="#v0.8" onclick="toggleCL('v0.8'); return false;">Toggle</a>)
					<ul class="changelog" id="v0.8">
						<li>Introduced nicer graphics which are disabled by default but can be enabled in the view menu</li>
						<li>Improved rotational behaviour: Map now always rotates around the center of the screen</li>
						<li>The amount filters (jumps, kills, belts, etc) now display amounts by color instead of dot size.</li>
						<li>Made the API data kills and jumps auto refresh when the cachetime expires (should be every hour)</li>
						<li>Other minor improvements</li>
						<li>Bugfix: routetab buttons were wrongly enabled/disabled</li>
						<li>Bugfix: displaybug when displaying no connection lines</li>
					</ul>
				</li>
				<li><strong>Version 0.7</strong> (<a href="#v0.7" onclick="toggleCL('v0.7'); return false;">Toggle</a>)
					<ul class="changelog" id="v0.7">
						<li>Fixed graphical bugs</li>
					</ul>
				</li>
				<li><strong>Version 0.6</strong> (<a href="#v0.6" onclick="toggleCL('v0.6'); return false;">Toggle</a>)
					<ul class="changelog" id="v0.6">
						<li>The map can now be rotated in 3D</li>
						<li>Added some pretty map moving animations</li>
						<li>All settings are now saved and restored</li>
						<li>Added mouse hover settings</li>
						<li>Added jumprange sphere</li>
						<li>Pathfinding is now running in a separate thread so the program doesnt freeze while calculating</li>
						<li>Tweaked pathfinding slightly</li>
						<li>Fixed route tab once again</li>
						<li>Removed map flattening</li>
						<li>Various small fixes and improvements</li>
					</ul>
				</li>
				<li><strong>Version 0.5</strong> (<a href="#v0.5" onclick="toggleCL('v0.5'); return false;">Toggle</a>)
					<ul class="changelog" id="v0.5">
						<li>Improved capital jump pathfinding performance slightly</li>
						<li>Capital jump pathfinding will now take distance in light years into account</li>
						<li>Added jump fuel usage to waypoint tooltips</li>
						<li>Added capital industrial to jump ships</li>
						<li>Added more information to solarsystem tooltips</li>
						<li>Added avoid solarsystem list which the routeplanner will try to avoid</li>
						<li>Fixed the clear route button on the route panel to actually do something :)</li>
						<li>Fixed amount of jumps always being one too high</li>
					</ul>
				</li>
				<li><strong>Version 0.4</strong> (<a href="#v0.4" onclick="toggleCL('v0.4'); return false;">Toggle</a>)
					<ul class="changelog" id="v0.4">
						<li>Added sovereignty and constellation sovereignty information</li>
						<li>Added planet, moon and belt count view</li>
						<li>Application will now show the amount of jumps/kills/etc in the hover info</li>
						<li>Added 'Prefer systems with solarsystems' option to route planner</li>
						<li>Removed solarsystem luminosity as it wasnt very useful</li>
					</ul>
				</li>
				<li><strong>Version 0.35</strong> (<a href="#v0.35" onclick="toggleCL('v0.35'); return false;">Toggle</a>)
					<ul class="changelog" id="v0.35">
						<li>Renamed drone regions to correct names</li>
						<li>Jumpdrive planner will now ignore unreachable regions</li>
						<li>Outposts are now loaded from the Eve Api</li>
					</ul>
				</li>
				<li><strong>Version 0.31</strong> (<a href="#v0.31" onclick="toggleCL('v0.31'); return false;">Toggle</a>)
					<ul class="changelog" id="v0.31">
						<li>Added fullscreen monitor selection</li>
					</ul>
				</li>
				<li><strong>Version 0.3</strong> (<a href="#v0.3" onclick="toggleCL('v0.3'); return false;">Toggle</a>)
					<ul class="changelog" id="v0.3">
						<li>First public release</li>
					</ul>
				</li>
			</ul>
		</div>
		
		<div id="screenshots">
			<h2>Screenshots:</h2>
			<br>
			<p><img src="screenshot.jpg"></p>
			<br>
			<p><img src="screenshot2.jpg"></p>
		</div>
	</div>
</div>

</body>
</html>
