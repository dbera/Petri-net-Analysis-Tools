# PnAT: Tools for Analysis and Visualization of Petri nets

PnAT is a set of tools to analyze and visualize Petri nets and and its reachability graph. It works well with [Yasper](http://www.yasper.org/), a modeling and simulation tool for Petri nets. The idea is that a user defines a Petri net model in Yasper and then uses PnAT in conjunction to analyze structural and behavioral properties. 

Some properties such as open petri nets, leg and choice properties are specific to models describing communicating component-based system. In such a system, components provide and consume services over interfaces. An interface is defined as a portnet [1]. A component net is defined in terms of refinements with provided and required portnets [1]. Such models can be modeled in Yasper using the subnets feature and interpreting reference places as interface places of a component. 

![GitHub Logo](/Images/yasper.png)

Beware that subnets more than one level deep are uninterpreted. Also ensure that all places, transitions and nets have labels. 

When you download PnAT, you will find two executable JAR files

  * **LaunchPetriNetAnalysis.jar**
    * Input: Accepts a Petri net model in PNML format (tested against models from [Yasper](http://www.yasper.org/) and [Tapaal](https://www.tapaal.net/)).
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
    
    ![GitHub Logo](/Images/PnATStructural.png)
      
  * **LaunchReachabilityGraphAnalysis.jar**
    * Input: Accepts a reachability graph file in DGS format (generated by **LaunchPetriNetAnalysis.jar**)
    * Analysis Options
      * Weak termination
      * strongly connected components
      * path finder: select a node in the visualization to see its marking and visualize a set of paths from the initial node and back to it
    * Other
      * reload file

    ![GitHub Logo](/Images/PnATBehavioral.png)
    
# References
 * [1] [Petri nets for Modeling Robots, PhD Thesis 2014, TU Eindhoven](https://research.tue.nl/en/publications/petri-nets-for-modeling-robots).
 * [2] [Designing Weakly Terminating ROS Systems, ATPN 2012](https://link.springer.com/chapter/10.1007/978-3-642-31131-4_18)
 * [3] [Component Framework where Port Compatibility implies Weak Termination, PNSE 2011](http://ceur-ws.org/Vol-723/paper11.pdf)

# Author
 * Debjyoti Bera, The Netherlands
 
# Contact
  For suggestions or questions you can mail me at bera82@gmail.com

## License

See the [LICENSE](LICENSE) file for license rights and limitations (GNU LGPL v2.1).
