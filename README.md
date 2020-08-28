# PnAT : Tools for Analysis and Visualization of Petri nets

PnAT is a set of tools to analyze and visualize Petri nets. In its current version, there are two executable JAR files, namely 
  * **LaunchPetriNetAnalysis.jar**
    * Input: Accepts a Petri net model in PNML format (tested against models from Yasper and Tapaal)
    * Analysis Options
      * state machine net
      * workflow net
      * open petri net
      * choice and leg properties
    * Generator Options
      * reachability graph (with bounds on number of steps)
      * coverability graph
      * skeleton 
    * Others
      * reload-file
  * **LaunchReachabilityGraphAnalysis.jar**
    * Analysis Options
      * Weak termination
      * strongly connected components
      * path finder: select a node in the visualization to see its marking and visualize a set of paths from the initial node and back to it
    * Other
      * reload file








Hubot is a framework to build chat bots, modeled after GitHub's Campfire bot of the same name, hubot.
He's pretty cool. He's [extendable with scripts](http://hubot.github.com/docs/#scripts) and can work
on [many different chat services](https://hubot.github.com/docs/adapters/).

This repository provides a library that's distributed by `npm` that you
use for building your own bots.  See the [documentation](http://hubot.github.com/docs)
for details on getting up and running with your very own robot friend.

In most cases, you'll probably never have to hack on this repo directly if you
are building your own bot. But if you do, check out [CONTRIBUTING.md](CONTRIBUTING.md)

If you'd like to chat with Hubot users and developers, [join us on Slack](https://hubot-slackin.herokuapp.com/).

## License

See the [LICENSE](LICENSE) file for license rights and limitations (MIT).
