<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet 	version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output method="xml" encoding="UTF-8" indent="yes"/>

	<!--  Include other xsl files -->
	<xsl:include href="BPMNElements.xsl"/>
	<xsl:include href="Aux_functions.xsl"/>
	<!-- / Include other xsl files -->
	
	<xsl:template name="SubnetFrameWork">
	<xsl:param name="id"/>
	<xsl:param name="name"/>
	<xsl:param name="px"/>
	<xsl:param name="py"/>
	<xsl:param name="description"/>
	<xsl:param name="annotation"/>
	<!-- Statistical Data -->
	<xsl:param name="processingTimeMean"/>
	<xsl:param name="processingTimeDeviation"/>	
	<xsl:param name="costVariable"/>
	<xsl:param name="costFixed"/>
	<!-- /Statistical Data -->
	<!-- Roles -->
	<xsl:param name="roleIDs"/>
	<!-- /Roles -->
	<!-- Optional Extra Level-->
	<xsl:param name="ref_inID"/>
	<xsl:param name="ref_outID"/>
	<!-- /Optional Extra Level-->
		
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
				</xsl:call-template>
			</xsl:if>
		</xsl:for-each>
	</xsl:if>	
	
		<xsl:element name="page">
			<xsl:attribute name="id">pa<xsl:value-of select="$id"/></xsl:attribute>
			<xsl:element name="name">
				<xsl:element name="text">
					<xsl:value-of select="$name"/>
				</xsl:element>
			</xsl:element>
			<xsl:element name="description">
				<xsl:element name="text">
					<xsl:if test="string-length($annotation) > 0">
						<xsl:text disable-output-escaping="yes">Annotation:	</xsl:text>
						<xsl:value-of select="$annotation"/>
						<xsl:text disable-output-escaping="yes">
						Description:	</xsl:text>
					</xsl:if>
					<xsl:value-of select="$description"/>
				</xsl:element>
			</xsl:element>
			<xsl:element name="graphics">
				<xsl:element name="position">
					<xsl:attribute name="x">
						<xsl:value-of select="$px"/>
					</xsl:attribute>
					<xsl:attribute name="y">
						<xsl:value-of select="$py"/>
					</xsl:attribute>
				</xsl:element>
				<xsl:element name="dimension">
					<xsl:attribute name="x">32</xsl:attribute>
					<xsl:attribute name="y">32</xsl:attribute>
				</xsl:element>
			</xsl:element>
			
			<xsl:call-template name="TransitionFrameWork">
				<xsl:with-param name="id"><xsl:value-of select="$id"/></xsl:with-param>
				<xsl:with-param name="name" select="$name"/>
				<xsl:with-param name="px" select="200"/>
				<xsl:with-param name="py" select="100"/>
				<xsl:with-param name="annotation" select="$annotation"/>
				<xsl:with-param name="processingTimeMean" select="$processingTimeMean"/>
				<xsl:with-param name="processingTimeDeviation" select="$processingTimeDeviation"/>
				<xsl:with-param name="costFixed" select="$costFixed"/>
				<xsl:with-param name="costVariable" select="$costVariable"></xsl:with-param>
				<xsl:with-param name="roleIDs" select="$roleIDs"/>
			</xsl:call-template>
			
			<xsl:choose>
				<xsl:when test="string-length($ref_outID) = 0">
					<xsl:for-each select="../../SequenceFlows/SequenceFlow[ translate(translate(Source,'(',''),')','') = $id ]">
						<!-- For all outgoing arcs -->
						<xsl:variable name="Target" select="Target"/>
						<xsl:call-template name="ReferencePlaceFrameWork">
							<xsl:with-param name="id">rp_ri<xsl:value-of select="$id"/>_<xsl:value-of select="count(preceding::SequenceFlow)"/></xsl:with-param>
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
									<xsl:when test="boolean(../../EndEvents/EndEvent/@id = $Target)">
										<xsl:value-of select="translate(translate(Target,'(',''),')','')"/>
									</xsl:when>			
									<xsl:when test="boolean(../../IntermediateEvents/IntermediateEvent/@id = $Target)">
										<xsl:value-of select="translate(translate(Target,'(',''),')','')"/>
									</xsl:when>	
									<xsl:otherwise>BOE</xsl:otherwise>
								</xsl:choose>
							</xsl:with-param>
							<xsl:with-param name="px" select="300"/>
							<xsl:with-param name="py" select="100"/>
						</xsl:call-template>
						<xsl:call-template name="ArcFrameWork">
							<xsl:with-param name="id">ar_ri<xsl:value-of select="$id"/>_<xsl:value-of select="position()"/></xsl:with-param>
							<xsl:with-param name="source">tr<xsl:value-of select="$id"/></xsl:with-param>
							<xsl:with-param name="target">rp_ri<xsl:value-of select="$id"/>_<xsl:value-of select="count(preceding::SequenceFlow)"/></xsl:with-param>
						</xsl:call-template>
					</xsl:for-each>
				</xsl:when>
				<xsl:otherwise>
					<xsl:call-template name="ReferencePlaceFrameWork">
						<xsl:with-param name="id">ref2<xsl:value-of select="$id"/></xsl:with-param>
						<xsl:with-param name="ref">pl2<xsl:value-of select="$ref_outID"/></xsl:with-param>
						<xsl:with-param name="px">300</xsl:with-param>
						<xsl:with-param name="py">100</xsl:with-param>
					</xsl:call-template>
					<xsl:call-template name="ArcFrameWork">
						<xsl:with-param name="id">ar1<xsl:value-of select="$id"/></xsl:with-param>
						<xsl:with-param name="source">tr<xsl:value-of select="$id"/></xsl:with-param>
						<xsl:with-param name="target">ref2<xsl:value-of select="$id"/></xsl:with-param>
					</xsl:call-template>
				</xsl:otherwise>
			</xsl:choose>		
			
			<xsl:choose>
			<xsl:when test="string-length($ref_inID) = 0">
				<xsl:for-each select="../../SequenceFlows/SequenceFlow[ translate(translate(Target,'(',''),')','') = $id ]">
					<!-- create only 1 referenceplace, and not for every incoming Sequence Flow -->
					<xsl:if test="position() = 1">
						<xsl:call-template name="ReferencePlaceFrameWork">
								<xsl:with-param name="id">rp_ro<xsl:value-of select="$id"/>_<xsl:value-of select="position()"/></xsl:with-param>
								<xsl:with-param name="ref">pl<xsl:value-of select="$id"/></xsl:with-param>
								<xsl:with-param name="px">50</xsl:with-param>
								<xsl:with-param name="py">50</xsl:with-param>
							</xsl:call-template>
							<xsl:call-template name="ArcFrameWork">
								<xsl:with-param name="id">ar_ro<xsl:value-of select="$id"/>_<xsl:value-of select="position()"/></xsl:with-param>
								<xsl:with-param name="source">rp_ro<xsl:value-of select="$id"/>_<xsl:value-of select="position()"/></xsl:with-param>
								<xsl:with-param name="target">tr<xsl:value-of select="$id"/></xsl:with-param>
							</xsl:call-template>
						</xsl:if>
				</xsl:for-each>
			</xsl:when>
				<xsl:otherwise>
					<xsl:call-template name="ReferencePlaceFrameWork">
						<xsl:with-param name="id">ref1<xsl:value-of select="$id"/></xsl:with-param>
						<xsl:with-param name="ref">pl1<xsl:value-of select="$ref_inID"/></xsl:with-param>
						<xsl:with-param name="px">50</xsl:with-param>
						<xsl:with-param name="py">100</xsl:with-param>
					</xsl:call-template>
					<xsl:call-template name="ArcFrameWork">
						<xsl:with-param name="id">arc<xsl:value-of select="$ref_inID"/></xsl:with-param>
						<xsl:with-param name="source">ref1<xsl:value-of select="$id"/></xsl:with-param>
						<xsl:with-param name="target">tr<xsl:value-of select="$id"/></xsl:with-param>
					</xsl:call-template>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:element>
	</xsl:template>
	
	<xsl:template name="TransitionFrameWork">
		<xsl:param name="id"/>
		<xsl:param name="name"/>
		<xsl:param name="type" select="'AND'"/>
		<xsl:param name="px"/>
		<xsl:param name="py"/>
		<xsl:param name="prefix"/>
		<xsl:param name="annotation"/>
		<!-- Statistical  Data-->
		<xsl:param name="processingTimeMean"/>
		<xsl:param name="processingTimeDeviation"/>
		<xsl:param name="costFixed"/>
		<xsl:param name="costVariable"/>
		<xsl:param name="weights"/>
		<!-- /Statistical Data-->
		<!-- Roles -->
		<xsl:param name="roleIDs"/>
		<!-- / Roles-->
		
		<xsl:element name="transition">
			<xsl:attribute name="id">
				<xsl:choose>
                			    <xsl:when test="string-length($prefix) > 0 ">
                			        <xsl:value-of select="$prefix"/>
                			    </xsl:when>
                			    <xsl:otherwise>tr</xsl:otherwise>
				</xsl:choose>
				<xsl:value-of select="$id"/>
			</xsl:attribute>
			<xsl:element name="name">
				<xsl:element name="text"><xsl:value-of select="$name"/></xsl:element>
			</xsl:element>
			<xsl:element name="description">
				<xsl:element name="text">
					<xsl:value-of select="$annotation"/>
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
			<xsl:element name="type">
				<xsl:element name="text"><xsl:value-of select="$type"/></xsl:element>
			</xsl:element>
			<!-- StatisticalData -->
			<xsl:if test="boolean(not(($processingTimeMean = 0)  and ($processingTimeDeviation = 0)  and ($costFixed = 0)  and ($costVariable = 0)))">
				<xsl:element name="toolspecific">
					<xsl:attribute name="tool">Yasper</xsl:attribute>
					<xsl:attribute name="version">1.2.1838.27475</xsl:attribute>
					<xsl:element name="processingTime" namespace="http://www.yasper.org/specs/epnml-1.1/toolspec">
						<xsl:element name="mean">
							<xsl:element name="text"><xsl:value-of select="$processingTimeMean"/></xsl:element>
						</xsl:element>
						<xsl:element name="deviation">
							<xsl:element name="text"><xsl:value-of select="$processingTimeDeviation"/></xsl:element>
						</xsl:element>
					</xsl:element>
					<xsl:element name="cost" namespace="http://www.yasper.org/specs/epnml-1.1/toolspec">
						<xsl:element name="fixed">
							<xsl:element name="text"><xsl:value-of select="$costFixed"/></xsl:element>
						</xsl:element>
						<xsl:element name="variable">
							<xsl:element name="text"><xsl:value-of select="$costVariable"/></xsl:element>
						</xsl:element>
					</xsl:element>
				</xsl:element>
			</xsl:if>
			<xsl:if test="string-length($weights) > 0">
				<xsl:element name="toolspecific">
					<xsl:attribute name="tool">Yasper</xsl:attribute>
					<xsl:attribute name="version">1.2.1838.27475</xsl:attribute>
					<xsl:element name="connectionWeights" namespace="http://www.yasper.org/specs/epnml-1.1/toolspec">
						<xsl:call-template name="ParseWeights">
							<xsl:with-param name="str" select="$weights"/>
							<xsl:with-param name="id" select="$id"/>
						</xsl:call-template>
					</xsl:element>
				</xsl:element>
			</xsl:if>
			<!-- /Statistic Data -->
			<!-- Roles -->
			<xsl:if test=" string-length($roleIDs) > 0 ">
				<xsl:element name="toolspecific">
					<xsl:attribute name="tool">Yasper</xsl:attribute>
					<xsl:attribute name="version">1.2.1838.27475</xsl:attribute>
					
					<xsl:element name="roles" namespace="http://www.yasper.org/specs/epnml-1.1/toolspec">
						<xsl:call-template name="ParseRoles">
							<xsl:with-param name="str" select="$roleIDs"/>
						</xsl:call-template>
					</xsl:element>	
				</xsl:element>
			</xsl:if>
			<!-- /Roles -->
		</xsl:element>
	</xsl:template>
	
	<xsl:template name="PlaceFrameWork">
		<xsl:param name="id"/>
		<xsl:param name="name"/>
		<xsl:param name="px"/>
		<xsl:param name="py"/>
		<xsl:param name="casesensitive" select=" 'true' "/>
		
		<xsl:element name="place">
			<xsl:attribute name="id"><xsl:value-of select="$id"/></xsl:attribute>
			<xsl:element name="name">
				<xsl:element name="text"><xsl:value-of select="$name"/></xsl:element>
			</xsl:element>
			<xsl:element name="graphics"><xsl:element name="position">
				<xsl:attribute name="x"><xsl:value-of select="$px"/></xsl:attribute>
				<xsl:attribute name="y"><xsl:value-of select="$py"/></xsl:attribute>
			</xsl:element>
				<xsl:element name="dimension">
					<xsl:attribute name="x">20</xsl:attribute>
					<xsl:attribute name="y">20</xsl:attribute>
				</xsl:element>
			</xsl:element>
			<!-- for case-sensitive places -->
			<xsl:if test="$casesensitive = 'true' ">
				<xsl:element name="toolspecific">
					<xsl:attribute name="tool">Yasper</xsl:attribute>
					<xsl:attribute name="version">1.2.1838.27475</xsl:attribute>
					<xsl:element name="tokenCaseSensitive" namespace="http://www.yasper.org/specs/epnml-1.1/toolspec">
						<xsl:element name="text">true</xsl:element>
					</xsl:element>
				</xsl:element>
			</xsl:if>
		</xsl:element>
	</xsl:template>
	
	<xsl:template name="ArcFrameWork">
		<xsl:param name="id"/>
		<xsl:param name="source"/>
		<xsl:param name="target"/>
		<!-- Points: list of x,y of type "x,y x,y x,y ..." where x is the x-position and y the y-position, dont forget the space after a pair-->
		<xsl:param name="points"/>
		
		<xsl:element name="arc">
			<xsl:attribute name="id"><xsl:value-of select="$id"/></xsl:attribute>
			<xsl:attribute name="source"><xsl:value-of select="$source"/></xsl:attribute>
			<xsl:attribute name="target"><xsl:value-of select="$target"/></xsl:attribute>
			<xsl:if test="string-length($points) > 0">
				<xsl:element name="graphics">
					<xsl:call-template name="ParsePoints">
						<xsl:with-param name="str" select="$points"/>
					</xsl:call-template>
				</xsl:element>
			</xsl:if>
		</xsl:element>
	</xsl:template>
	
	<xsl:template name="ReferencePlaceFrameWork">
		<xsl:param name="id"/>
		<xsl:param name="ref"/>
		<xsl:param name="px"/>
		<xsl:param name="py"/>
		
		<xsl:element name="referencePlace">
			<xsl:attribute name="id"><xsl:value-of select="$id"/></xsl:attribute>
			<xsl:attribute name="ref"><xsl:value-of select="$ref"/></xsl:attribute>
			<xsl:element name="graphics">
				<xsl:element name="position">
					<xsl:attribute name="x"><xsl:value-of select="$px"/></xsl:attribute>
					<xsl:attribute name="y"><xsl:value-of select="$py"/></xsl:attribute>
				</xsl:element>
				<xsl:element name="dimension">
					<xsl:attribute name="x">20</xsl:attribute>
					<xsl:attribute name="y">20</xsl:attribute>
				</xsl:element>
			</xsl:element>
		</xsl:element>
	</xsl:template>	
	
	<xsl:template name="GatewayFrameWork">
		<xsl:param name="id"/>
		<xsl:param name="name"/>
		<xsl:param name="px"/>
		<xsl:param name="py"/>
		<xsl:param name="annotation"/>
		<!-- Statistical Data-->
		<!-- id,weight id,weight ..(ex: "pl1212,2 pl34,3 ")-->
		<xsl:param name="weights"/>
		<!-- /Statistical Data-->
		<!-- Roles -->
		<xsl:param name="roleIDs"/>
		<!-- /Roles -->
		
		
		<!--PARALLEL AND-->
		<xsl:if test="GatewayType = 'ParallelAND'" >
			<xsl:call-template name="TransitionFrameWork">
				<xsl:with-param name="id"><xsl:value-of select="$id"/></xsl:with-param>
				<xsl:with-param name="name"><xsl:value-of select="$name"/></xsl:with-param>
				<xsl:with-param name="px"><xsl:value-of select="$px"/></xsl:with-param>
				<xsl:with-param name="py"><xsl:value-of select="$py"/>	</xsl:with-param>
				<xsl:with-param name="annotation" select="$annotation"/>
				<xsl:with-param name="roleIDs" select="$roleIDs"/>
			</xsl:call-template>
		</xsl:if>
		
		<!--DateBasedXOR-->
		<xsl:if test="GatewayType = 'DataBasedXOR'">
			<xsl:call-template name="TransitionFrameWork">
				<xsl:with-param name="id"><xsl:value-of select="$id"/></xsl:with-param>
				<xsl:with-param name="name"><xsl:value-of select="$name"/></xsl:with-param>
				<xsl:with-param name="px"><xsl:value-of select="$px"/></xsl:with-param>
				<xsl:with-param name="py"><xsl:value-of select="$py"/></xsl:with-param>
				<xsl:with-param name="type">XOR</xsl:with-param>
				<xsl:with-param name="annotation" select="$annotation"/>
				<xsl:with-param name="roleIDs" select="$roleIDs"/>
				<xsl:with-param name="weights" select="$weights"/>
			</xsl:call-template>
		</xsl:if>		
		
		<!--LogicalOR-->
		<xsl:if test="GatewayType = 'InclusiveOR' ">
			<!--<xsl:element name="page">
				<xsl:attribute name="id">tr<xsl:value-of select="$id"/></xsl:attribute>
				<xsl:element name="name">
					<xsl:element name="text">InclusiveOR</xsl:element>
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
				
				<xsl:call-template name="TransitionFrameWork">
					<xsl:with-param name="id">A<xsl:value-of select="$id"/></xsl:with-param>
					<xsl:with-param name="name"></xsl:with-param>
					<xsl:with-param name="px">100</xsl:with-param>
					<xsl:with-param name="py">100</xsl:with-param>
					<xsl:with-param name="annotation"></xsl:with-param>
					<xsl:with-param name="type">AND</xsl:with-param>
				</xsl:call-template>
				
				<xsl:for-each select="../../SequenceFlows/SequenceFlow[ translate(translate(Source,'(',''),')','') = $id ]">
					<xsl:variable name="Target" select="Target"/>
				
				<xsl:call-template name="PlaceFrameWork">
					<xsl:with-param name="id">pl1<xsl:value-of select="$id"/>_<xsl:value-of select="position()"/></xsl:with-param>
					<xsl:with-param name="px">150</xsl:with-param>
					<xsl:with-param name="py"><xsl:value-of select="100 + (position() -1) * 50"/></xsl:with-param>
				</xsl:call-template>
					
				<xsl:call-template name="TransitionFrameWork">
					<xsl:with-param name="id">B<xsl:value-of select="$id"/>_<xsl:value-of select="position()"/></xsl:with-param>
					<xsl:with-param name="name">HIER</xsl:with-param>
					<xsl:with-param name="px">200</xsl:with-param>
					<xsl:with-param name="py"><xsl:value-of select="100 + (position() -1)  * 50"/></xsl:with-param>
					<xsl:with-param name="annotation"></xsl:with-param>
					<xsl:with-param name="type">XOR</xsl:with-param>
				</xsl:call-template>
					
				<xsl:call-template name="ArcFrameWork">
					<xsl:with-param name="id">AB<xsl:value-of select="$id"/>_<xsl:value-of select="position()"/></xsl:with-param>
					<xsl:with-param name="source">trA<xsl:value-of select="$id"/></xsl:with-param>
					<xsl:with-param name="target">pl1<xsl:value-of select="$id"/>_<xsl:value-of select="position()"/></xsl:with-param>
				</xsl:call-template>
					
				<xsl:call-template name="ArcFrameWork">
					<xsl:with-param name="id">BC<xsl:value-of select="$id"/>_<xsl:value-of select="position()"/></xsl:with-param>
					<xsl:with-param name="source">pl1<xsl:value-of select="$id"/>_<xsl:value-of select="position()"/></xsl:with-param>
					<xsl:with-param name="target">trB<xsl:value-of select="$id"/>_<xsl:value-of select="position()"/></xsl:with-param>
				</xsl:call-template>
					
				</xsl:for-each>
				
			</xsl:element>-->
			<xsl:call-template name="TransitionFrameWork">
				<xsl:with-param name="id"><xsl:value-of select="$id"/></xsl:with-param>
				<xsl:with-param name="name">InclusiceOR!!!</xsl:with-param>
				<xsl:with-param name="px"><xsl:value-of select="$px"/></xsl:with-param>
				<xsl:with-param name="py"><xsl:value-of select="$py"/></xsl:with-param>
				<xsl:with-param name="type">XOR</xsl:with-param>
				<xsl:with-param name="annotation" select="$annotation"/>
				<xsl:with-param name="roleIDs" select="$roleIDs"/>
			</xsl:call-template>
		</xsl:if>
		
		<xsl:if test="GatewayType = 'EventBasedXOR' ">
			<xsl:call-template name="TransitionFrameWork">
				<xsl:with-param name="id"><xsl:value-of select="$id"/></xsl:with-param>
				<xsl:with-param name="name">EventBasedXOR!!!</xsl:with-param>
				<xsl:with-param name="px"><xsl:value-of select="$px"/></xsl:with-param>
				<xsl:with-param name="py"><xsl:value-of select="$py"/></xsl:with-param>
				<xsl:with-param name="type">XOR</xsl:with-param>
				<xsl:with-param name="annotation" select="$annotation"/>
				<xsl:with-param name="roleIDs" select="$roleIDs"/>
			</xsl:call-template>
		</xsl:if>
		
		<xsl:if test="GatewayType = 'ComplexDecision' ">
			<xsl:call-template name="TransitionFrameWork">
				<xsl:with-param name="id"><xsl:value-of select="$id"/></xsl:with-param>
				<xsl:with-param name="name">ComplexDecision!!!</xsl:with-param>
				<xsl:with-param name="px"><xsl:value-of select="$px"/></xsl:with-param>
				<xsl:with-param name="py"><xsl:value-of select="$py"/></xsl:with-param>
				<xsl:with-param name="type">XOR</xsl:with-param>
				<xsl:with-param name="annotation" select="$annotation"/>
				<xsl:with-param name="roleIDs" select="$roleIDs"/>
			</xsl:call-template>
		</xsl:if>			
		
	</xsl:template>

	<!--TEMPLATE CALLS-->
	
	<xsl:template match="/BPMN/BusinessProcessDiagram/PoolCollection">
		<xsl:element name="pnml">
			<xsl:element name="net">
				<xsl:attribute name="type"><xsl:text>http://www.yasper.org/specs/epnml-1.1</xsl:text></xsl:attribute>
				<xsl:attribute name="id"><xsl:text>tn1</xsl:text></xsl:attribute>
				<xsl:element name="name">
					<xsl:element name="text"><xsl:value-of select="../Name/text()"/></xsl:element>
				</xsl:element>
				
				<xsl:for-each select="../AllAvailableFunctionalGroups">
					<xsl:element name="toolspecific">
						<xsl:attribute name="tool">Yasper</xsl:attribute>
						<xsl:attribute name="version">1.2.1838.27475</xsl:attribute>
						
						<xsl:element name="roles" namespace="http://www.yasper.org/specs/epnml-1.1/toolspec">
							<xsl:for-each select="FunctionalGroup/RoleNames/RoleName">
								<xsl:element name="role">
									<xsl:attribute name="id">rl<xsl:value-of select="@id"/></xsl:attribute>
									<xsl:element name="name">
										<xsl:element name="text">
											<xsl:value-of select="."/>
										</xsl:element>
									</xsl:element>
									<xsl:element name="actor"/>
								</xsl:element>
							</xsl:for-each>
							</xsl:element>
					</xsl:element>
				</xsl:for-each>
				
				<xsl:for-each select="Pool/Process/BPMNElements">
					<xsl:call-template name="Tasks"/>
					<xsl:call-template name="Gateways"/>
					<xsl:call-template name="SubProcesses"/>
					<xsl:call-template name="StartEvents"/>
					<xsl:call-template name="IntermediateEvents"/>
					<xsl:call-template name="EndEvents"/>
				</xsl:for-each>
			</xsl:element>
		</xsl:element>
	</xsl:template>
		
	<!--Disable implict templates-->
	<xsl:template match="@*|text()"/>
	
</xsl:stylesheet>
