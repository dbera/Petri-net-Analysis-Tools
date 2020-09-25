<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
<xsl:import href="Aux_functions.xsl"/>
	
	<xsl:template match="Gateways" name="Gateways">
		<xsl:for-each select="Gateways/Gateway">
			<xsl:variable name="name"><xsl:value-of select="Name"/></xsl:variable>
			<xsl:variable name="px"><xsl:value-of select="MiddlePoint/@X"/></xsl:variable>
			<xsl:variable name="py"><xsl:value-of select="MiddlePoint/@Y"/></xsl:variable>
			<xsl:variable name="id" select="translate(translate(@id,'(',''),')','')"/>
			<xsl:variable name="realID" select="@id"/>
			<!-- Statistical Data -->
			<xsl:variable name="weights">
				<xsl:for-each select="StatisticalData/OutputWeight">
					<xsl:value-of select="translate(translate(@OutputElementId,'(',''),')','')"/>,<xsl:value-of select="Weight"/><xsl:text> </xsl:text>
				</xsl:for-each>
			</xsl:variable>
			<!-- /Statistical Data -->
			
			<!-- Roles -->
			<xsl:variable name="roleIDs">
				<xsl:for-each select="AdditionalInfo/Roles/FunctionalGroup/RoleNames/RoleName">
					<!-- roleIDs separated with comma's-->
					<xsl:text>rl</xsl:text><xsl:value-of select="@id"/>
					<xsl:if test="position() != last() ">
						<xsl:text>@</xsl:text>	
					</xsl:if>
				</xsl:for-each>
			</xsl:variable>
			<!-- /Roles -->
			
			<!-- Check incoming Sequence Flows-->
			<xsl:for-each select="../../SequenceFlows/SequenceFlow[ translate(translate(Target,'(',''),')','') = $id]">
				<xsl:variable name="count" select="count(preceding::SequenceFlow)"/>
				<xsl:variable name="SourceID" select="Source"/>
				<xsl:call-template name="PlaceFrameWork">
					<xsl:with-param name="id">pl<xsl:value-of select="$id"/>_<xsl:value-of select="$count"/></xsl:with-param>
					<xsl:with-param name="name"/>
					<xsl:with-param name="px">
						<xsl:call-template name="GetPositionX">
							<xsl:with-param name="X" select="$px"/>
							<xsl:with-param name="id" select="$SourceID"/>
						</xsl:call-template>
					</xsl:with-param>
					<xsl:with-param name="py">
						<xsl:call-template name="GetPositionY">
							<xsl:with-param name="Y" select="$py"/>
							<xsl:with-param name="id" select="$SourceID"/>
						</xsl:call-template>
					</xsl:with-param>
				</xsl:call-template>	
				<xsl:call-template name="ArcFrameWork">
					<xsl:with-param name="id">ar<xsl:value-of select="$id"/>_<xsl:value-of select="$count"/></xsl:with-param>
					<xsl:with-param name="source">pl<xsl:value-of select="$id"/>_<xsl:value-of select="$count"/></xsl:with-param>
					<xsl:with-param name="target">tr<xsl:value-of select="$id"/></xsl:with-param>
				</xsl:call-template>
			</xsl:for-each>
			
			<!-- Check outgoing Sequence Flows-->
			<xsl:for-each select="../../SequenceFlows/SequenceFlow[ translate(translate(Source,'(',''),')','') = $id]">
				<xsl:variable name="Target"><xsl:value-of select="Target"/></xsl:variable>
				<xsl:variable name="count" select="count(preceding::SequenceFlow)"/>
				<xsl:call-template name="ArcFrameWork">
					<xsl:with-param name="id">ar<xsl:value-of select="$id"/>_<xsl:value-of select="$count"/></xsl:with-param>
					<xsl:with-param name="source">tr<xsl:value-of select="$id"/></xsl:with-param>
					<xsl:with-param name="target">
						<xsl:call-template name="GetPlace">
							<xsl:with-param name="FromID">
								<xsl:value-of select="$Target"/>
							</xsl:with-param>
							<xsl:with-param name="count" select="$count"/>
						</xsl:call-template>
					</xsl:with-param>
				</xsl:call-template>
			</xsl:for-each>
		
			<xsl:call-template name="GatewayFrameWork">
				<xsl:with-param name="id" select="$id"/>
				<xsl:with-param name="name" select="$name"/>
				<xsl:with-param name="px" select="$px"/>
				<xsl:with-param name="py" select="$py"/>
				<xsl:with-param name="annotation">
					<xsl:call-template name="Annotation">
						<xsl:with-param name="FromID" select="$realID"/>
					</xsl:call-template>
				</xsl:with-param>
				<xsl:with-param name="weights" select="$weights"/>
				<xsl:with-param name="roleIDs" select="$roleIDs"/>
			</xsl:call-template>
		</xsl:for-each>
	</xsl:template>
	
	<xsl:template match="Tasks" name="Tasks">
		<xsl:for-each select="Tasks/Task">
			<xsl:variable name="name"><xsl:value-of select="Name"/></xsl:variable>
			<xsl:variable name="px"><xsl:value-of select="MiddlePoint/@X"/></xsl:variable>
			<xsl:variable name="py"><xsl:value-of select="MiddlePoint/@Y"/></xsl:variable>
			<xsl:variable name="realID"><xsl:value-of select="@id"/></xsl:variable>
			<xsl:variable name="id"><xsl:value-of select="translate(translate(@id,'(',''),')','')"/></xsl:variable>
			<xsl:variable name="description" select="Description"/>
			
			<!-- Statistical data-->
			<xsl:variable name="processingTimeMean" select="StatisticalData/ProcessingTimeMean"/>
			<xsl:variable name="processingTimeDeviation" select="StatisticalData/ProcessingTimeDeviation"/>
			<xsl:variable name="costFixed" select="StatisticalData/CostFixed"/>
			<xsl:variable name="costVariable" select="StatisticalData/CostVariable"/>
			<!-- /Statistical data -->
			
			<!-- Roles -->
			<xsl:variable name="roleIDs">
				<xsl:for-each select="AdditionalInfo/Roles/FunctionalGroup/RoleNames/RoleName">
					<!-- roleIDs separated with comma's-->
					<xsl:text>rl</xsl:text><xsl:value-of select="@id"/>
					<xsl:if test="position() != last()">
						<xsl:text>@</xsl:text>	
					</xsl:if>
				</xsl:for-each>
			</xsl:variable>
			<!-- /Roles -->
			
			<!-- FORCED LOOP INTERNAL MARKER -->
			<xsl:choose>
			<xsl:when test="Description = 'LoopMarker' ">
				
				<!-- place -->
				<xsl:if test="count(../../SequenceFlows/SequenceFlow[ translate(translate(Target,'(',''),')','') = $id]) > 0">
					<xsl:for-each select="../../SequenceFlows/SequenceFlow[ Target = $realID]">
						<xsl:if test="position() = 1">
							<xsl:variable name="SourceID" select="Source"/>
							<xsl:call-template name="PlaceFrameWork">
								<xsl:with-param name="id">pl<xsl:value-of select="$id"/></xsl:with-param>
								<xsl:with-param name="px">
									<xsl:call-template name="GetPositionX">
										<xsl:with-param name="X" select="$px"/>
										<xsl:with-param name="id" select="$SourceID"/>
									</xsl:call-template>
								</xsl:with-param>
								<xsl:with-param name="py">
									<xsl:call-template name="GetPositionY">
										<xsl:with-param name="Y" select="$py"/>
										<xsl:with-param name="id" select="$SourceID"/>
									</xsl:call-template>
								</xsl:with-param>
								<xsl:with-param name="name" select="pl"/>
							</xsl:call-template>
						</xsl:if>
					</xsl:for-each>
				</xsl:if>				
				
				<!-- Add extra level-->
				<xsl:element name="page">
					<xsl:attribute name="id">pa<xsl:value-of select="translate(translate($realID,'(',''),')','')"/></xsl:attribute>
					<xsl:element name="name">
						<xsl:element name="text">Loop</xsl:element>
					</xsl:element>
					<xsl:element name="graphics">
						<xsl:element name="position">
							<xsl:attribute name="x"><xsl:value-of select="$px"/></xsl:attribute>
							<xsl:attribute name="y"><xsl:value-of select="$py"/></xsl:attribute>
						</xsl:element>
						<xsl:element name="dimension">
							<xsl:attribute name="x">32</xsl:attribute>
							<xsl:attribute name="y">32</xsl:attribute>
						</xsl:element>
					</xsl:element>
					
					<xsl:if test="count(../../SequenceFlows/SequenceFlow[ translate(translate(Target,'(',''),')','') = $id]) > 0">
						<xsl:call-template name="ReferencePlaceFrameWork">
							<xsl:with-param name="id">ref<xsl:value-of select="$id"/></xsl:with-param>
							<xsl:with-param name="ref">pl<xsl:value-of select="$id"/></xsl:with-param>
							<xsl:with-param name="px" select="50"/>
							<xsl:with-param name="py" select="100"/>
						</xsl:call-template>
						<xsl:call-template name="ArcFrameWork">
							<xsl:with-param name="id">arc1<xsl:value-of select="$id"/></xsl:with-param>
							<xsl:with-param name="source">ref<xsl:value-of select="$id"/></xsl:with-param>
							<xsl:with-param name="target">trtrSTART<xsl:value-of select="$id"/></xsl:with-param>
						</xsl:call-template>	
					</xsl:if>
					
					<xsl:call-template name="TransitionFrameWork">
						<xsl:with-param name="id">trSTART<xsl:value-of select="$id"/></xsl:with-param>
						<xsl:with-param name="name">Start</xsl:with-param>
						<xsl:with-param name="px" select="100"/>
						<xsl:with-param name="py" select="100"/>
					</xsl:call-template>
					
					<xsl:call-template name="PlaceFrameWork">
						<xsl:with-param name="id">pl1<xsl:value-of select="$id"/></xsl:with-param>
						<xsl:with-param name="name" select="'PL1'"/>
						<xsl:with-param name="px">250</xsl:with-param>
						<xsl:with-param name="py">100</xsl:with-param>
					</xsl:call-template>
					
					<xsl:call-template name="ArcFrameWork">
						<xsl:with-param name="id">arc2<xsl:value-of select="$id"/></xsl:with-param>
						<xsl:with-param name="source">trtrSTART<xsl:value-of select="$id"/></xsl:with-param>
						<xsl:with-param name="target">pl1<xsl:value-of select="$id"/></xsl:with-param>
					</xsl:call-template>	
					
					<!-- The actual subnet, with extra level params-->
					<xsl:call-template name="SubnetFrameWork">
						<xsl:with-param name="id">Loop<xsl:value-of select="translate(translate(@id,'(',''),')','')"/></xsl:with-param>
						<xsl:with-param name="name" select="$name"/>
						<xsl:with-param name="px" select="300"/>
						<xsl:with-param name="py" select="100"/>
						<xsl:with-param name="annotation">
							<xsl:call-template name="Annotation">
								<xsl:with-param name="FromID" select="$realID"/>
							</xsl:call-template>
						</xsl:with-param> 
						<!-- Statistical Data -->
					<xsl:with-param name="processingTimeMean" select="$processingTimeMean"/>
						<xsl:with-param name="processingTimeDeviation" select="$processingTimeDeviation"/>
						<xsl:with-param name="costFixed" select="$costFixed"/>
						<xsl:with-param name="costVariable" select="$costVariable"/> 
						<!-- /Statistical Data -->
						<!-- Roles -->
					<xsl:with-param name="roleIDs" select="$roleIDs"></xsl:with-param> 
						<!-- /Roles -->
						<!-- Optional Extra Level -->
					<xsl:with-param name="ref_inID"><xsl:value-of select="$id"/></xsl:with-param>
						<xsl:with-param name="ref_outID"><xsl:value-of select="$id"/></xsl:with-param> 
						<!-- /Optional Extra Level -->
					</xsl:call-template>
					
					<xsl:call-template name="PlaceFrameWork">
						<xsl:with-param name="id">pl2<xsl:value-of select="$id"/></xsl:with-param>
						<xsl:with-param name="name" select="'PL2'"/>
						<xsl:with-param name="px">350</xsl:with-param>
						<xsl:with-param name="py">100</xsl:with-param>
					</xsl:call-template>
					
					<xsl:call-template name="TransitionFrameWork">
						<xsl:with-param name="id">tr3<xsl:value-of select="$id"/></xsl:with-param>
						<xsl:with-param name="name">XOR</xsl:with-param>
						<xsl:with-param name="px">450</xsl:with-param>
						<xsl:with-param name="py">100</xsl:with-param>
						<xsl:with-param name="type">XOR</xsl:with-param>
					</xsl:call-template>
					
					<!-- PL2 to Tr3 -->
					<xsl:call-template name="ArcFrameWork">
						<xsl:with-param name="id">arc3<xsl:value-of select="$id"/></xsl:with-param>
						<xsl:with-param name="source">pl2<xsl:value-of select="$id"/></xsl:with-param>
						<xsl:with-param name="target">trtr3<xsl:value-of select="$id"/></xsl:with-param>
					</xsl:call-template>
					
					<xsl:call-template name="PlaceFrameWork">
						<xsl:with-param name="id">pl3<xsl:value-of select="$id"/></xsl:with-param>
						<xsl:with-param name="name" select="'PL3'"/>
						<xsl:with-param name="px">500</xsl:with-param>
						<xsl:with-param name="py">100</xsl:with-param>
					</xsl:call-template>
					
					<!-- tr3 to PL3 -->
					<xsl:call-template name="ArcFrameWork">
						<xsl:with-param name="id">arc4<xsl:value-of select="$id"/></xsl:with-param>
						<xsl:with-param name="source">trtr3<xsl:value-of select="$id"/></xsl:with-param>
						<xsl:with-param name="target">pl3<xsl:value-of select="$id"/></xsl:with-param>
					</xsl:call-template>
					
					<!-- Pl3 to TrEnd -->
					<xsl:call-template name="ArcFrameWork">
						<xsl:with-param name="id">arc5<xsl:value-of select="$id"/></xsl:with-param>
						<xsl:with-param name="source">pl3<xsl:value-of select="$id"/></xsl:with-param>
						<xsl:with-param name="target">trtrEND<xsl:value-of select="$id"/></xsl:with-param>
					</xsl:call-template>
					
					<!-- XOR to pl1 -->
					<xsl:call-template name="ArcFrameWork">
						<xsl:with-param name="id">arc6<xsl:value-of select="$id"/></xsl:with-param>
						<xsl:with-param name="source">trtr3<xsl:value-of select="$id"/></xsl:with-param>
						<xsl:with-param name="target">pl1<xsl:value-of select="$id"/></xsl:with-param>
						<xsl:with-param name="points">450,38 250,38 </xsl:with-param>
					</xsl:call-template>
					
					<!-- End Subnet Transition-->
					<xsl:call-template name="TransitionFrameWork">
						<xsl:with-param name="id">trEND<xsl:value-of select="$id"/></xsl:with-param>
						<xsl:with-param name="name">End</xsl:with-param>
						<xsl:with-param name="px">550</xsl:with-param>
						<xsl:with-param name="py">100</xsl:with-param>
					</xsl:call-template>
					
					<!-- Needed for output...-->
					<xsl:for-each select="../../SequenceFlows/SequenceFlow[ translate(translate(Source,'(',''),')','') = $id ] ">
						<xsl:variable name="Target" select="Target"/>
						<xsl:variable name="count" select="count(preceding::SequenceFlows)"/>
						<xsl:call-template name="ReferencePlaceFrameWork">
							<xsl:with-param name="id">ref<xsl:value-of select="$id"/>_<xsl:value-of select="$count"/></xsl:with-param>
							<xsl:with-param name="ref">
								<xsl:call-template name="GetPlace">
									<xsl:with-param name="FromID">
										<xsl:value-of select="$Target"/>
									</xsl:with-param>
									<xsl:with-param name="count" select="$count"/>
								</xsl:call-template>
							</xsl:with-param>
							<xsl:with-param name="px">600</xsl:with-param>
							<xsl:with-param name="py"><xsl:value-of select="50 + (50 * position() )"/></xsl:with-param>
						</xsl:call-template>
						
						<xsl:call-template name="ArcFrameWork">
							<xsl:with-param name="id">arc<xsl:value-of select="$id"/>_<xsl:value-of select="position()"/></xsl:with-param>
							<xsl:with-param name="source">trtrEND<xsl:value-of select="$id"/></xsl:with-param>
							<xsl:with-param name="target">ref<xsl:value-of select="$id"/>_<xsl:value-of select="$count"/></xsl:with-param>
						</xsl:call-template>					
					</xsl:for-each>					
					<!-- /Needed for output-->
				</xsl:element>
			</xsl:when>
			<xsl:otherwise>
			
			<!-- /FORCED TIMER AT BOUNDARY -->
			
			<xsl:call-template name="SubnetFrameWork">
				<xsl:with-param name="id" select="translate(translate(@id,'(',''),')','')"/>
				<xsl:with-param name="name" select="$name"/>
				<xsl:with-param name="px" select="$px"/>
				<xsl:with-param name="py" select="$py"/>
				<xsl:with-param name="description" select="$description"/>
				<xsl:with-param name="annotation">
					<xsl:call-template name="Annotation">
						<xsl:with-param name="FromID" select="$realID"/>
					</xsl:call-template>
				</xsl:with-param>
				<xsl:with-param name="processingTimeMean" select="$processingTimeMean"/>
				<xsl:with-param name="processingTimeDeviation" select="$processingTimeDeviation"/>
				<xsl:with-param name="costFixed" select="$costFixed"/>
				<xsl:with-param name="costVariable" select="$costVariable"/>
				<xsl:with-param name="roleIDs" select="$roleIDs"></xsl:with-param>
			</xsl:call-template>
				
			</xsl:otherwise>
			</xsl:choose>
		
		</xsl:for-each>		
	</xsl:template>
	
	<xsl:template match="SubProcesses" name="SubProcesses">
		<xsl:for-each select="SubProcesses/SubProcess">
			<xsl:variable name="px" select="MiddlePoint/@X"/>
			<xsl:variable name="py" select="MiddlePoint/@Y"/>
			<xsl:variable name="id" select="translate(translate(@id,'(',''),')','')"/>
			<xsl:variable name="realID" select="@id"/>
			<xsl:variable name="name" select="Name"/>
			<xsl:variable name="description" select="Description"/>
			
			<xsl:variable name="EndEventX">
				<xsl:choose>
				<xsl:when test="boolean(BPMNElements/EndEvents/EndEvent/MiddlePoint/@X)">
					<xsl:value-of select="BPMNElements/EndEvents/EndEvent/MiddlePoint/@X"/>
				</xsl:when>
					<xsl:otherwise>0</xsl:otherwise>
				</xsl:choose>	
			</xsl:variable>
			<xsl:variable name="EndEventY">
				<xsl:choose>
				<xsl:when test="boolean(BPMNElements/EndEvents/EndEvent/MiddlePoint/@Y)">
					<xsl:value-of select="BPMNElements/EndEvents/EndEvent/MiddlePoint/@Y"/>
				</xsl:when>
					<xsl:otherwise>0</xsl:otherwise>
				</xsl:choose>	
			</xsl:variable>
			<xsl:variable name="EndEventID" select="translate(translate(BPMNElements/EndEvents/EndEvent/@id,'(',''),')','')"/>
				
			
			<xsl:if test="count(../../SequenceFlows/SequenceFlow[ translate(translate(Target,'(',''),')','') = $id]) > 0">
				<xsl:for-each select="../../SequenceFlows/SequenceFlow[ translate(translate(Target,'(',''),')','') = $id]">
					<xsl:if test="position() = 1">
						<xsl:variable name="SourceID" select="Source"/>
						<xsl:call-template name="PlaceFrameWork">
							<xsl:with-param name="id">pl<xsl:value-of select="$id"/></xsl:with-param>
							<xsl:with-param name="px">
								<xsl:call-template name="GetPositionX">
									<xsl:with-param name="X" select="$px"/>
									<xsl:with-param name="id" select="$SourceID"/>
								</xsl:call-template>
							</xsl:with-param>
							<xsl:with-param name="py">
								<xsl:call-template name="GetPositionY">
									<xsl:with-param name="Y" select="$py"/>
									<xsl:with-param name="id" select="$SourceID"/>
								</xsl:call-template>
							</xsl:with-param>
							<xsl:with-param name="name" select="pl"/>
						</xsl:call-template>
					</xsl:if>
				</xsl:for-each>
			</xsl:if>	
			
			<xsl:element name="page">
				<xsl:attribute name="id">sn<xsl:value-of select="$id"/></xsl:attribute>
				
				<xsl:element name="name">
					<xsl:element name="text"><xsl:value-of select="$name"/></xsl:element>
				</xsl:element>
				<xsl:element name="description">
					<xsl:element name="text">
						<xsl:variable name="annotation">
							<xsl:call-template name="Annotation">
								<xsl:with-param name="FromID" select="$realID"/>
							</xsl:call-template>
						</xsl:variable>
						<xsl:if test="string-length($annotation) > 0">
							<xsl:text disable-output-escaping="yes">Annotation:	</xsl:text>
							<xsl:value-of select="$annotation"/>
							<xsl:text disable-output-escaping="yes">
							</xsl:text>
						</xsl:if>
						<xsl:text disable-output-escaping="yes">Description:	</xsl:text>
						<xsl:value-of select="$description"/>
					</xsl:element>
				</xsl:element>
				
				<xsl:element name="graphics">
					<xsl:element name="position">
						<xsl:attribute name="x"><xsl:value-of select="$px"/></xsl:attribute>
						<xsl:attribute name="y"><xsl:value-of select="$py"/></xsl:attribute>
					</xsl:element>
					<xsl:element name="dimension">
						<xsl:attribute name="x">32</xsl:attribute>
						<xsl:attribute name="y">32</xsl:attribute>
					</xsl:element>
				</xsl:element>			
				
				<!-- check outgoing SequenceFlows-->
				<!-- Create referenceplace and connect with standard transition -->
				<xsl:for-each select="../../SequenceFlows/SequenceFlow[ translate(translate(Source,'(',''),')','') = $id]">
					<xsl:variable name="Source" select="Source"/>
					<xsl:variable name="Target" select="Target"/>
				               <xsl:variable name="isbiflow" select="IsBiflow"/>
					
					<xsl:call-template name="ReferencePlaceFrameWork">
						<xsl:with-param name="id">rp_si<xsl:value-of select="$id"/>_<xsl:value-of select="position()"/></xsl:with-param>
						<xsl:with-param name="ref"><xsl:text>pl</xsl:text>
							<xsl:choose>
								<xsl:when test="boolean( ../../Tasks/Task/@id = $Target )">
									<xsl:value-of select="translate(translate(Target,'(',''),')','')"/>
								</xsl:when>
								<xsl:when test="boolean( ../../Gateways/Gateway/@id = $Target )">
									<xsl:value-of select="translate(translate(Target,'(',''),')','')"/>_<xsl:value-of select="count(preceding::SequenceFlow)"/>
								</xsl:when>
								<xsl:when test="boolean( ../../SubProcesses/SubProcess/@id = $Target )">
									<xsl:value-of select="translate(translate(Target,'(',''),')','')"/>
								</xsl:when>
								<xsl:when test="boolean(../../StartEvents/StartEvent/@id = $Target)">
									<xsl:value-of select="translate(translate(Target,'(',''),')','')"/>
								</xsl:when>
								<xsl:when test="boolean(../../IntermediateEvents/IntermediateEvent/@id = $Target)">
									<xsl:value-of select="translate(translate(Target,'(',''),')','')"/>
								</xsl:when>	
								<xsl:when test="boolean(../../EndEvents/EndEvent/@id = $Target)">
									<xsl:value-of select="translate(translate(Target,'(',''),')','')"/>
								</xsl:when>	
							</xsl:choose>
						</xsl:with-param>
						<xsl:with-param name="px" select="$EndEventX + 50"/>
						<xsl:with-param name="py" select="$EndEventY"/>
					</xsl:call-template>
					<!-- Connect referenceplace with End Transition-->
					<xsl:call-template name="ArcFrameWork">
						<xsl:with-param name="id">ar_si<xsl:value-of select="$id"/>_<xsl:value-of select="position()"/></xsl:with-param>
						<xsl:with-param name="source">trEnd_<xsl:value-of select="$EndEventID"/></xsl:with-param>
						<xsl:with-param name="target">rp_si<xsl:value-of select="$id"/>_<xsl:value-of select="position()"/></xsl:with-param>
					</xsl:call-template>
				</xsl:for-each>
				
				<!-- process all the elements it contains-->
				<xsl:for-each select="BPMNElements">
					<xsl:call-template name="Tasks"/>		
					<xsl:call-template name="Gateways"/>
					<xsl:call-template name="SubProcesses"/>
					<xsl:call-template name="StartEvents"/>
					<xsl:call-template name="IntermediateEvents"/>
					<xsl:call-template name="EndEvents"/>
				</xsl:for-each>
			</xsl:element>
		</xsl:for-each>
	</xsl:template>
	
	<xsl:template match="StartEvents" name="StartEvents">
		<xsl:for-each select="StartEvents/StartEvent">
			<xsl:variable name="id" select="translate(translate(@id,'(',''),')','')"/>
			<xsl:variable name="px" select="MiddlePoint/@X"/>
			<xsl:variable name="py" select="MiddlePoint/@Y"/>
			<xsl:variable name="name" select="Name"/>
			<xsl:variable name="ProcessName" select="../../../Name"/>
			<xsl:variable name="SubProcessID" select="translate(translate(../../../@id,'(',''),')','')"/>
			<!-- Statistical Data -->
			<xsl:variable name="mean" select="StatisticalData/ProcessingTimeMean"/>
			<xsl:variable name="deviation" select="StatisticalData/ProcessingTimeDeviation"/>
			<!-- /Statistical Data -->
		
			<xsl:choose>
                			<xsl:when test="string-length($SubProcessID) = 0">
                			<!--Create an Emitor -->	
                			    <xsl:element name="transition">
                					<xsl:attribute name="id">Start_<xsl:value-of select="$id"/></xsl:attribute>
                					<xsl:element name="name">
                						<xsl:element name="text">
                							<xsl:value-of select="$name"/>
                						</xsl:element>
                					</xsl:element>
                					<xsl:element name="graphics">
                						<xsl:element name="position">
                							<xsl:attribute name="x"><xsl:value-of select="$px"/></xsl:attribute>
                							<xsl:attribute name="y"><xsl:value-of select="$py"/></xsl:attribute>
                						</xsl:element>
                						<xsl:element name="dimension">
                							<xsl:attribute name="x">32</xsl:attribute>
                							<xsl:attribute name="y">32</xsl:attribute>
                						</xsl:element>
                					</xsl:element>
                					<xsl:element name="toolspecific">
                						<xsl:attribute name="tool">Yasper</xsl:attribute>
                						<xsl:attribute name="version">1.2.1844.40720</xsl:attribute>
                						<xsl:element name="emitor" namespace="http://www.yasper.org/specs/epnml-1.1/toolspec">
                							<xsl:element name="text">true</xsl:element>
                						</xsl:element>
                						<xsl:if test="$mean > 0 or $deviation > 0">
                							<xsl:element name="processingTime" namespace="http://www.yasper.org/specs/epnml-1.1/toolspec">
                								<xsl:element name="mean">
                									<xsl:element name="text">
                										<xsl:value-of select="$mean"/>
                									</xsl:element>
                								</xsl:element>
                								<xsl:element name="deviation">
                									<xsl:element name="text">
                										<xsl:value-of select="$deviation"/>
                									</xsl:element>
                								</xsl:element>
                							</xsl:element>
                						</xsl:if>
                					</xsl:element>
                				</xsl:element>
                			</xsl:when>
                			<xsl:otherwise>
                				<xsl:call-template name="TransitionFrameWork">
                					<xsl:with-param name="id" select="$id"/>
                					<xsl:with-param name="prefix">Start_</xsl:with-param>
                					<xsl:with-param name="name">Start Event</xsl:with-param>
                					<xsl:with-param name="px" select="$px"/>
                					<xsl:with-param name="py" select="$py"/>
                					<xsl:with-param name="annotation">
                						<xsl:call-template name="Annotation">
                							<xsl:with-param name="FromID" select="$id"/>
                						</xsl:call-template>
                					</xsl:with-param>
                				</xsl:call-template>
                			</xsl:otherwise>
			</xsl:choose>
			<xsl:if test="boolean(../../../../../SequenceFlows/SequenceFlow[ translate(translate( Target ,'(','' ), ')', '') = $SubProcessID ])">
				<xsl:call-template name="ReferencePlaceFrameWork">
					<xsl:with-param name="id">ref<xsl:value-of select="$id"/></xsl:with-param>
					<xsl:with-param name="ref">pl<xsl:value-of select="$SubProcessID "/></xsl:with-param>
					<xsl:with-param name="px" select="$px - 50"/>
					<xsl:with-param name="py" select="$py"/>
				</xsl:call-template>
				<xsl:call-template name="ArcFrameWork">
					<xsl:with-param name="id">ar<xsl:value-of select="$id"/></xsl:with-param>
					<xsl:with-param name="source">ref<xsl:value-of select="$id"/></xsl:with-param>
					<xsl:with-param name="target">Start_<xsl:value-of select="$id"/></xsl:with-param>
				</xsl:call-template>
			</xsl:if>
			<xsl:for-each select="../../SequenceFlows/SequenceFlow[ translate(translate(Source,'(',''),')','') = $id ]">		
				<xsl:variable name="Target" select="Target"/>
				<xsl:call-template name="ArcFrameWork">
					<xsl:with-param name="id">ar<xsl:value-of select="$id"/>_<xsl:value-of select="count(preceding::SequenceFlow)"/></xsl:with-param>
					<xsl:with-param name="source">Start_<xsl:value-of select="$id"/></xsl:with-param>
					<xsl:with-param name="target">
						<xsl:call-template name="GetPlace">
							<xsl:with-param name="FromID" select="$Target"/>
							<xsl:with-param name="count"><xsl:value-of select="count(preceding::SequenceFlow)"/></xsl:with-param>
						</xsl:call-template>
					</xsl:with-param>
				</xsl:call-template>
			</xsl:for-each>
		</xsl:for-each>
	</xsl:template>
	
    <xsl:template match="EndEvents" name="EndEvents">
        <xsl:variable name="SubProcessID" select="translate(translate(../../SubProcess/@id,'(',''),')','')"/>
        <xsl:for-each select="EndEvents/EndEvent">
            <xsl:variable name="id" select="translate(translate(@id,'(',''),')','')"/>
            <xsl:variable name="name" select="Name"/>
            <xsl:variable name="px" select="MiddlePoint/@X"/>
            <xsl:variable name="py" select="MiddlePoint/@Y"/>		
            
            <!-- Check if Collector must be created -->
            <xsl:choose>
                <xsl:when test="string-length($SubProcessID) = 0">
                    <!-- Create Collector -->
                    <xsl:element name="transition">
                        <xsl:attribute name="id">trEnd_<xsl:value-of select="$id"/></xsl:attribute>
                        <xsl:element name="name">
                            <xsl:element name="text">
                                <xsl:value-of select="$name"/>	
                            </xsl:element>
                        </xsl:element>
                        <xsl:element name="graphics">
                            <xsl:element name="position">
                                <xsl:attribute name="x"><xsl:value-of select="$px"/></xsl:attribute>
                                <xsl:attribute name="y"><xsl:value-of select="$py"/></xsl:attribute>
                            </xsl:element>
                            <xsl:element name="dimension">
                                <xsl:attribute name="x">32</xsl:attribute>
                                <xsl:attribute name="y">32</xsl:attribute>
                            </xsl:element>
                        </xsl:element>
                        <xsl:element name="toolspecific">
                            <xsl:attribute name="tool">Yasper</xsl:attribute>
                            <xsl:attribute name="version">1.2.1844.40720</xsl:attribute>
                            <xsl:element name="collector" namespace="http://www.yasper.org/specs/epnml-1.1/toolspec">
                                <xsl:element name="text">true</xsl:element>
                            </xsl:element>
                        </xsl:element>
                    </xsl:element>
                </xsl:when>
                <xsl:otherwise>
                    <!-- Create End Transition -->
                    <xsl:call-template name="TransitionFrameWork">
                        <xsl:with-param name="prefix">trEnd_</xsl:with-param>
                        <xsl:with-param name="id" select="$id"/>
                        <xsl:with-param name="name">End Event</xsl:with-param>
                        <xsl:with-param name="px" select="$px"/>
                        <xsl:with-param name="py" select="$py"/>
                        <xsl:with-param name="annotation">
                            <xsl:call-template name="Annotation">
                                <xsl:with-param name="FromID" select="$id"/>
                            </xsl:call-template>
                        </xsl:with-param>
                    </xsl:call-template>
                </xsl:otherwise>
            </xsl:choose>
            
            <!-- in both cases -->
            <xsl:for-each select="../../SequenceFlows/SequenceFlow[ translate(translate(Target,'(',''),')','') = $id ]">	
                <xsl:variable name="SourceID" select="Source"/>
                <xsl:variable name="count"><xsl:value-of select="count(preceding::SequenceFlow)"/></xsl:variable>
                <xsl:if test="position() = 1">
                    <xsl:call-template name="PlaceFrameWork">
                        <xsl:with-param name="id">pl<xsl:value-of select="$id"/></xsl:with-param>
                        <xsl:with-param name="px">
                            <xsl:call-template name="GetPositionX">
                                <xsl:with-param name="X" select="$px"/>
                                <xsl:with-param name="id" select="$SourceID"/>
                            </xsl:call-template>
                        </xsl:with-param>
                        <xsl:with-param name="py">
                            <xsl:call-template name="GetPositionY">
                                <xsl:with-param name="Y" select="$py"/>
                                <xsl:with-param name="id" select="$SourceID"/>
                            </xsl:call-template>
                        </xsl:with-param>
                    </xsl:call-template>
                    <!-- Connect the place with the End Event transition -->
                    <xsl:call-template name="ArcFrameWork">
                        <xsl:with-param name="id">ar<xsl:value-of select="$id"/>_<xsl:value-of select="$count"/></xsl:with-param>
                        <xsl:with-param name="source">pl<xsl:value-of select="$id"/></xsl:with-param>
                        <xsl:with-param name="target">
                            <xsl:text>trEnd_</xsl:text><xsl:value-of select="$id"/>
                        </xsl:with-param>
                    </xsl:call-template>
                  </xsl:if>
            </xsl:for-each>
        </xsl:for-each>
        <xsl:if test="count(EndEvents/EndEvent) = 0">
            <xsl:if test="boolean(../../../SequenceFlows/SequenceFlow[ translate(translate( Source ,'(','' ), ')', '') = $SubProcessID ])">
                <!-- no End Event, but BPMN parent process has outgoing Sequence Flow -->
                <xsl:call-template name="TransitionFrameWork">
                    <xsl:with-param name="id">trEnd_</xsl:with-param>
                    <xsl:with-param name="name">End_Event</xsl:with-param>
                    <xsl:with-param name="px" select="300"/>
                    <xsl:with-param name="py" select="50"/>
                </xsl:call-template>
            </xsl:if>
        </xsl:if>
    </xsl:template>
	
	<xsl:template name="IntermediateEvents" match="IntermediateEvents">
		<xsl:for-each select="IntermediateEvents/IntermediateEvent">
			<xsl:variable name="id" select="translate(translate(@id,'(',''),')','')"/>
			<xsl:variable name="px" select="MiddlePoint/@X"/>
			<xsl:variable name="py" select="MiddlePoint/@Y"/>
			<xsl:variable name="name" select="Name"/>
			
			<xsl:choose>
				<!-- Translations depends on the Type of the Trigger-->
				<xsl:when test="Trigger = 'No' ">
					<xsl:call-template name="SubnetFrameWork">
						<xsl:with-param name="id" select="$id"/>
						<xsl:with-param name="name" select="$name"/>
						<xsl:with-param name="px" select="$px"/>
						<xsl:with-param name="py" select="$py"/>
					</xsl:call-template>
				</xsl:when>
				<xsl:when test="Trigger = 'Timer' ">
					
					<xsl:call-template name="SubnetFrameWork">
						<xsl:with-param name="id" select="$id"/>
						<xsl:with-param name="name">
							<xsl:choose>
								<xsl:when test=" string-length($name)  = 0">
									<xsl:text>Timer</xsl:text>
								</xsl:when>
								<xsl:otherwise>
									<xsl:value-of select="$name"/>
								</xsl:otherwise>
							</xsl:choose>
						</xsl:with-param>
						<xsl:with-param name="px" select="$px"/>
						<xsl:with-param name="py" select="$py"/>
					</xsl:call-template>
				
				</xsl:when>
				<xsl:when test="Trigger = 'Rule' ">
					<xsl:call-template name="TransitionFrameWork">
						<xsl:with-param name="name" select="$name"/>
						<xsl:with-param name="id" select="$id"/>
						<xsl:with-param name="px" select="$px"/>
						<xsl:with-param name="py" select="$py"/>
					</xsl:call-template>
				</xsl:when>
				<xsl:when test="Trigger = 'Message' or 'Compensation' ">
					<!-- Transition and place (receive or send message)-->
					<!-- Check if type is receive or send-->
					<xsl:call-template name="TransitionFrameWork">
						<xsl:with-param name="name" select="$name"/>
						<xsl:with-param name="id" select="$id"/>
						<xsl:with-param name="px" select="$px"/>
						<xsl:with-param name="py" select="$py"/>
					</xsl:call-template>
					<xsl:call-template name="PlaceFrameWork">
						<xsl:with-param name="id">pl<xsl:value-of select="$id"/></xsl:with-param>
						<xsl:with-param name="px" select="$px - 50"/>
						<xsl:with-param name="py" select="$py"/>
					</xsl:call-template>
					<xsl:call-template name="ArcFrameWork">
						<xsl:with-param name="id">ar_ie<xsl:value-of select="$id"/></xsl:with-param>
						<xsl:with-param name="source">pl<xsl:value-of select="$id"/></xsl:with-param>
						<xsl:with-param name="target">tr<xsl:value-of select="$id"/></xsl:with-param>
					</xsl:call-template>	
					<!-- extra place for receiving or sending a message-->
					<xsl:call-template name="PlaceFrameWork">
						<xsl:with-param name="id">pl_ie<xsl:value-of select="$id"/></xsl:with-param>
						<xsl:with-param name="px" select="$px"/>
						<xsl:with-param name="py" select="$py - 50"/>
						<xsl:with-param name="casesensitive" select="false"/>
					</xsl:call-template>
					<xsl:call-template name="ArcFrameWork">
						<xsl:with-param name="id">ar_ie2<xsl:value-of select="$id"/></xsl:with-param>
						<xsl:with-param name="source">tr<xsl:value-of select="$id"/></xsl:with-param>
						<xsl:with-param name="target">pl_ie<xsl:value-of select="$id"/></xsl:with-param>
					</xsl:call-template>
					<xsl:for-each select="../../SequenceFlows/SequenceFlow[ translate(translate(Source,'(',''),')','') = $id ] ">
						<xsl:variable name="Target" select="Target"/>
						<xsl:call-template name="ArcFrameWork">
							<xsl:with-param name="id">ar<xsl:value-of select="$id"/>_<xsl:value-of select="count(preceding::SequenceFlow)"/></xsl:with-param>
							<xsl:with-param name="source">tr<xsl:value-of select="$id"/></xsl:with-param>
							<xsl:with-param name="target">
								<xsl:call-template name="GetPlace">
									<xsl:with-param name="FromID" select="$Target"/>
									<xsl:with-param name="pos"><xsl:value-of select="count(preceding::SequenceFlow)"/></xsl:with-param>
								</xsl:call-template>
							</xsl:with-param>
						</xsl:call-template>
					</xsl:for-each>
				</xsl:when>
				<xsl:when test="Trigger = 'Error' "></xsl:when>
				<xsl:when test="Trigger = 'Cancel' "></xsl:when>
				<!--<xsl:when test="Trigger = 'Compensation' "></xsl:when>-->
				<xsl:when test="Trigger = 'Link' "></xsl:when>
				<xsl:when test="Trigger = 'Multiple' "></xsl:when>
				<xsl:when test="Trigger = 'Terminate' "></xsl:when>
			</xsl:choose>
		</xsl:for-each>
	</xsl:template>
	
</xsl:stylesheet>
