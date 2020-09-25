<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"  
	xmlns:tool="http://www.yasper.org/specs/epnml-1.1/toolspec" >
	
<xsl:template match="* | @* ">
	<xsl:copy>
		<xsl:apply-templates select="node() | @*"/>
	</xsl:copy>
</xsl:template>
	
<xsl:template match="page">
	<xsl:variable name="pageID" select="@id"/>
	<xsl:choose>
		<xsl:when test=" count(transition) = 1 and count(page) = 0 and count(place) = 0">
			<xsl:for-each select="referencePlace">
				<xsl:variable name="id" select="@id"/>
				<xsl:variable name="ref" select="@ref"/>
				
				<xsl:for-each select="../arc[ @source = $id ]">
					<xsl:element name="arc">
						<xsl:attribute name="id"><xsl:value-of select="@id"/></xsl:attribute>
						<xsl:attribute name="source">
							<xsl:value-of select="$ref"/>
						</xsl:attribute>
						<xsl:attribute name="target">
							<xsl:value-of select="$pageID"/>
						</xsl:attribute>
						<xsl:if test="boolean(type/text)">
							<xsl:element name="type">
								<xsl:element name="text">
									<xsl:value-of select="type/text"/>
								</xsl:element>
							</xsl:element>
						</xsl:if>
						<xsl:if test="../referencePlace[ @id = $id ]/toolspecific/tool:pathGraphics">
							<xsl:element name="graphics">
								<xsl:for-each select="../referencePlace[ @id = $id ]/toolspecific/tool:pathGraphics/tool:position">
									<xsl:element name="position">
										<xsl:attribute name="x">
											<xsl:value-of select="@x" />
										</xsl:attribute>
										<xsl:attribute name="y">
											<xsl:value-of select="@y" />
										</xsl:attribute>
									</xsl:element>
								</xsl:for-each>
							</xsl:element>
						</xsl:if>		
					</xsl:element>
				</xsl:for-each>
				
					<xsl:for-each select="../arc[ @target = $id ]">
						<xsl:element name="arc">
							<xsl:attribute name="id"><xsl:value-of select="@id"/></xsl:attribute>
							<xsl:attribute name="source">
								<xsl:value-of select="$pageID"/>
							</xsl:attribute>
							<xsl:attribute name="target">
								<xsl:value-of select="$ref"/>
							</xsl:attribute>
							<xsl:if test="boolean(type/text)">
								<xsl:element name="type">
									<xsl:element name="text">
										<xsl:value-of select="type/text"/>
									</xsl:element>
								</xsl:element>
							</xsl:if>
							<xsl:if test="../referencePlace[ @id = $id ]/toolspecific/tool:pathGraphics">
								<xsl:element name="graphics">
									<xsl:for-each select="../referencePlace[ @id = $id ]/toolspecific/tool:pathGraphics/tool:position">
										<xsl:element name="position">
											<xsl:attribute name="x">
												<xsl:value-of select="@x" />
											</xsl:attribute>
											<xsl:attribute name="y">
												<xsl:value-of select="@y" />
											</xsl:attribute>
										</xsl:element>
									</xsl:for-each>
								</xsl:element>
							</xsl:if>
						</xsl:element>
					</xsl:for-each>
			</xsl:for-each>
			
			<xsl:element name="transition">
				<xsl:attribute name="id">
					<xsl:value-of select="@id"/>
				</xsl:attribute>
				<xsl:element name="name">
					<xsl:element name="text">
						<xsl:value-of select="name/text"/>
					</xsl:element>
				</xsl:element>
				<xsl:element name="description">
					<xsl:element name="text">
						<xsl:value-of select="description/text"/>
					</xsl:element>
				</xsl:element>
				<xsl:element name="graphics">
					<xsl:element name="position">
						<xsl:attribute name="x"><xsl:value-of select="graphics/position/@x"/></xsl:attribute>
						<xsl:attribute name="y"><xsl:value-of select="graphics/position/@y"/></xsl:attribute>
					</xsl:element>
					<xsl:element name="dimension">
						<xsl:attribute name="x"><xsl:value-of select="graphics/dimension/@x"/></xsl:attribute>
						<xsl:attribute name="y"><xsl:value-of select="graphics/dimension/@y"/></xsl:attribute>
					</xsl:element>
				</xsl:element>
				<xsl:if test="boolean(transition/type/text)">
					<xsl:element name="type">
						<xsl:element name="text">
							<xsl:value-of select="transition/type/text"/>
						</xsl:element>
					</xsl:element>
				</xsl:if>
				<xsl:for-each select="transition/toolspecific">
					<xsl:copy>
						<xsl:apply-templates select="node() | @*"/>
					</xsl:copy>
				</xsl:for-each>
			</xsl:element>
		</xsl:when>
		<xsl:otherwise>
			<xsl:copy>
				<xsl:apply-templates select="node() | @*"/>
			</xsl:copy>
		</xsl:otherwise>
	</xsl:choose>
</xsl:template>
	
	
	
</xsl:stylesheet>
