<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	
	<xsl:template name="GetPlace">
		<xsl:param name="FromID"/>
		<xsl:param name="count"/>
		
		<xsl:for-each select="../../Tasks/Task[ @id = $FromID ]">
			<xsl:text>pl</xsl:text><xsl:value-of select="translate(translate($FromID,'(',''),')','')"/>
		</xsl:for-each>
		<xsl:for-each select="../../Gateways/Gateway[ @id = $FromID ]">
			<xsl:text>pl</xsl:text><xsl:value-of select="translate(translate($FromID,'(',''),')','')"/>_<xsl:value-of select="$count"/>
		</xsl:for-each>
		<xsl:for-each select="../../SubProcesses/SubProcess[ @id = $FromID ]">
			<xsl:text>pl</xsl:text><xsl:value-of select="translate(translate($FromID,'(',''),')','')"/>
		</xsl:for-each>
		<xsl:for-each select="../../StartEvents/StartEvent[ @id = $FromID ]">
			<xsl:text>pl</xsl:text><xsl:value-of select="translate(translate($FromID,'(',''),')','')"/>
		</xsl:for-each>
		<xsl:for-each select="../../IntermediateEvents/IntermediateEvent[ @id = $FromID ] ">
			<xsl:text>pl</xsl:text><xsl:value-of select="translate(translate($FromID,'(',''),')','')"/>
		</xsl:for-each>
		<xsl:for-each select="../../EndEvents/EndEvent[ @id = $FromID ]">
			<xsl:text>pl</xsl:text><xsl:value-of select="translate(translate($FromID,'(',''),')','')"/>
		</xsl:for-each>
	</xsl:template>
	
	<xsl:template name="GetPositionX">
		<xsl:param name="X"/>
		<xsl:param name="id"/>
		
		<xsl:variable name="TargetX">
			<xsl:choose>
				<xsl:when test="boolean(../../Tasks/Task[ @id = $id ]/MiddlePoint/@X)">
					<xsl:value-of select="../../Tasks/Task[ @id = $id ]/MiddlePoint/@X"/>			
				</xsl:when>
				<xsl:when test="boolean(../../Gateways/Gateway[  @id = $id ]/MiddlePoint/@X)">
					<xsl:value-of select="../../Gateways/Gateway[ @id = $id ]/MiddlePoint/@X"/>			
				</xsl:when>	
				<xsl:when test="boolean(../../SubProcesses/SubProcess[ @id = $id ]/MiddlePoint/@X)">
					<xsl:value-of select="../../SubProcesses/SubProcess[ @id = $id ]/MiddlePoint/@X"/>			
				</xsl:when>	
				<xsl:when test="boolean(../../StartEvents/StartEvent[ @id = $id ]/MiddlePoint/@X)">
					<xsl:value-of select="../../StartEvents/StartEvent[ @id = $id ]/MiddlePoint/@X"/>
				</xsl:when>
				<xsl:when test="boolean(../../EndEvents/EndEvent[ @id = $id ]/MiddlePoint/@X)">
					<xsl:value-of select="../../EndEvents/EndEvent[ @id = $id ]/MiddlePoint/@X"/>
				</xsl:when>	
				<xsl:when test="boolean(../../IntermediateEvents/IntermediateEvent[ @id = $id ]/MiddlePoint/@X)">
					<xsl:value-of select="../../IntermediateEvents/IntermediateEvent[ @id = $id ]/MiddlePoint/@X"/>
				</xsl:when>
			</xsl:choose>
		</xsl:variable>		
		<xsl:value-of select="round(($TargetX + $X) * 0.5)"/>
	</xsl:template>
	
	<xsl:template name="GetPositionY">
		<xsl:param name="Y"></xsl:param>
		<xsl:param name="id"/>
		
		<xsl:variable name="TargetY">
			<xsl:choose>
				<xsl:when test="boolean(../../Tasks/Task[ @id = $id ]/MiddlePoint/@Y)">
					<xsl:value-of select="../../Tasks/Task[ @id = $id ]/MiddlePoint/@Y"/>			
				</xsl:when>
				<xsl:when test="boolean(../../Gateways/Gateway[ @id = $id ]/MiddlePoint/@Y)">
					<xsl:value-of select="../../Gateways/Gateway[ @id = $id ]/MiddlePoint/@Y"/>			
				</xsl:when>	
				<xsl:when test="boolean(../../SubProcesses/SubProcess[ @id = $id ]/MiddlePoint/@Y)">
					<xsl:value-of select="../../SubProcesses/SubProcess[ @id = $id ]/MiddlePoint/@Y"/>			
				</xsl:when>	
				<xsl:when test="boolean(../../StartEvents/StartEvent[ @id = $id ]/MiddlePoint/@Y)">
					<xsl:value-of select="../../StartEvents/StartEvent[ @id = $id ]/MiddlePoint/@Y"/>
				</xsl:when>
				<xsl:when test="boolean(../../EndEvents/EndEvent[ @id = $id ]/MiddlePoint/@Y)">
					<xsl:value-of select="../../EndEvents/EndEvent[ @id = $id ]/MiddlePoint/@Y"/>
				</xsl:when>
				<xsl:when test="boolean(../../IntermediateEvents/IntermediateEvent[ @id = $id ]/MiddlePoint/@Y)">
					<xsl:value-of select="../../IntermediateEvents/IntermediateEvent[ @id = $id ]/MiddlePoint/@Y"/>
				</xsl:when>				
			</xsl:choose>
		</xsl:variable>		
		<xsl:value-of select="round(($TargetY + $Y) * 0.5)"/>
	</xsl:template>
	
	<xsl:template name="Annotation">
		<xsl:param name="FromID"/>
		
		<xsl:for-each select="../../AssociationFlows/AssociationFlow[ Target = $FromID ]">
			<xsl:variable name="Source" select="Source"/>			
			<xsl:for-each select="../../Annotations/Annotation[ @id = $Source ]">
				<xsl:value-of select="Name"/>
			</xsl:for-each>
		</xsl:for-each>
		
		<xsl:for-each select="../../AssociationFlows/AssociationFlow[ Source = $FromID ]">
			<xsl:variable name="Target" select="Target"/>
			<xsl:for-each select="../../Annotations/Annotation[ @id = $Target ]">
				<xsl:value-of select="Name"/>
			</xsl:for-each>
		</xsl:for-each>
	
	</xsl:template>
	
	<xsl:template name="ParseRoles">
		<xsl:param name="str"/>
		
		<xsl:choose>
			<xsl:when test="contains($str,'@')">
				<xsl:element name="role">
					<xsl:attribute name="ref">
						<xsl:value-of select="substring-before($str,'@')"/>
					</xsl:attribute>
				</xsl:element>
				<xsl:call-template name="ParseRoles">
					<xsl:with-param name="str" select="substring-after($str,'@')"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:choose>
					<xsl:when test="string-length($str) > 0 ">
						<xsl:element name="role">
							<xsl:attribute name="ref">
								<xsl:value-of select="$str"/>
							</xsl:attribute>
							</xsl:element>
					</xsl:when>
				</xsl:choose>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>	
	
	<xsl:template name="ParsePoints">
		<xsl:param name="str"/>
		
		<xsl:element name="position">
			<xsl:attribute name="x">
				<xsl:value-of select="substring-before(substring-before($str,' '),',' ) "/>
			</xsl:attribute>
			<xsl:attribute name="y">
				<xsl:value-of select="substring-after(substring-before($str,' '),',' ) "/>
			</xsl:attribute>
		</xsl:element>
		<xsl:if test="string-length( substring-after($str,' ') ) > 0 ">
			<xsl:call-template name="ParsePoints">
				<xsl:with-param name="str" select="substring-after($str,' ')"/>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>
	
	<xsl:template name="ParseWeights">
		<xsl:param name="str"/>
		<xsl:param name="id"/>
		
		<xsl:variable name="position">
			<xsl:if test="boolean(../../Gateways/Gateway[ translate(translate( @id,'(',''),')','') = substring-before(substring-before($str,' '),',') ])">
				<xsl:for-each select="../../SequenceFlows/SequenceFlow[ translate(translate(Target,'(',''),')','') = substring-before(substring-before($str,' '),',')  ]">
					<xsl:if test=" translate(translate(Source,'(',''),')','') = $id ">
						<xsl:value-of select="count(preceding::SequenceFlow)"/>
					</xsl:if>
				</xsl:for-each>
			</xsl:if>
		</xsl:variable>
		
		<xsl:element name="connectionWeight">
			<xsl:attribute name="connection">
				<xsl:text>pl</xsl:text><xsl:value-of select="substring-before(substring-before($str,' '), ',') "/>
				<xsl:if test="string-length($position) > 0">_<xsl:value-of select="$position"/></xsl:if>
			</xsl:attribute>
			<xsl:element name="weight">
				<xsl:element name="text">
					<xsl:value-of select="substring-after(substring-before($str,' '),',' ) "/>				
				</xsl:element>
			</xsl:element>
		</xsl:element>
		<xsl:if test="string-length( substring-after($str,' ') ) > 0 ">
			<xsl:call-template name="ParseWeights">
				<xsl:with-param name="str" select="substring-after($str,' ')"/>
				<xsl:with-param name="id" select="$id"/>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>	
	
</xsl:stylesheet>
