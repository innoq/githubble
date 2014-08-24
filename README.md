# GitHubble


The Idea of this Web-App is to discover the GitHub Galaxy.
You start at a fixed point (e.g. your GitHub Account) and travel throughout the GitHub Repositories.

There are several kinds of nodes:

* User - the basic GitHub User Account (identified by Name and Avatar)
* Organisation - Organisations hold Teams of Users (identified by Name and Avatar)
* Repositories - small Nodes owned by Organisations or Users.

Concepts are still in Development.

Uses

* d3.js
* Play2 Java
* public GitHub Api

Started at innoQ Summer of Code 16./17. August 2014.


# Concepts

The orbital Display Methods is:

for Users: (((( user ) orgas ) repos ) followers )
for Repos: ((( repo ) forks ) contributors )
for Orgas: (((  orga ) repos ) members )

Because we are not using the GitHub API Pagination Methods (at the moment), we just displaying the Entries (Follower,Repos, Members) from the first Page.
