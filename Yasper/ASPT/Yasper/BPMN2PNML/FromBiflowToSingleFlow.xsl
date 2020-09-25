<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"  
    xmlns:tool="http://www.yasper.org/specs/epnml-1.1/toolspec" >
    <xsl:output method="xml" encoding="UTF-8" indent="no"/>
    
    <xsl:template match="* | @* ">
        <xsl:copy>
            <xsl:apply-templates select="node() | @*"/>
        </xsl:copy>
    </xsl:template>
    
    <xsl:template match="SequenceFlow">
        <xsl:choose>
            <xsl:when test="IsBiflow = 'true' ">
                <xsl:element name="SequenceFlow">
                    <xsl:attribute name="id"><xsl:value-of select="@id"/></xsl:attribute> 
                    <xsl:copy-of select="Name"/>
                    <xsl:copy-of select="Description"/>
                    <xsl:copy-of select="Source"/>
                    <xsl:copy-of select="Target"/>
                    <xsl:copy-of select="Path"/>
                    <xsl:copy-of select="Quantity"/>
                    <xsl:element name="IsBiflow">false</xsl:element>
                </xsl:element>
                <xsl:element name="SequenceFlow">
                    <xsl:attribute name="id">(<xsl:value-of select="generate-id(.)"/>-<xsl:value-of select="count(preceding::SequenceFlow)"/>)</xsl:attribute>
                    <xsl:copy-of select="Name"/>
                    <xsl:copy-of select="Description"/>
                    <xsl:element name="Source"><xsl:value-of select="Target"/></xsl:element>
                    <xsl:element name="Target"><xsl:value-of select="Source"/></xsl:element>
                    <xsl:element name="Path">
                            <xsl:variable name="numberOfPathElements" select="count(Path/PathElement)"/>
                            <xsl:call-template name="reversePathElementOrder">
                                <xsl:with-param name="numberOfPathElements" select="$numberOfPathElements"/>
                            </xsl:call-template>
                    </xsl:element>
                    <xsl:copy-of select="Quantity"/>
                    <xsl:element name="IsBiflow">false</xsl:element>
                </xsl:element>
            </xsl:when>
            <xsl:otherwise>
                <xsl:copy>
                    <xsl:apply-templates select="node() | @*"/>
                </xsl:copy>
            </xsl:otherwise>
            </xsl:choose>
    </xsl:template>
    
    <xsl:template name="reversePathElementOrder">
        <xsl:param name="numberOfPathElements"/>
        
        <xsl:choose>
            <xsl:when test="$numberOfPathElements > 0">
                <xsl:element name="PathElement">
                    <xsl:attribute name="X"><xsl:value-of select="Path/PathElement[$numberOfPathElements]/@X"/></xsl:attribute>
                    <xsl:attribute name="Y"><xsl:value-of select="Path/PathElement[$numberOfPathElements]/@Y"/></xsl:attribute>
                </xsl:element>
                <xsl:call-template name="reversePathElementOrder">
                    <xsl:with-param name="numberOfPathElements" select="$numberOfPathElements - 1"/>
                </xsl:call-template>                
            </xsl:when>
        </xsl:choose>
    </xsl:template>
    
    
</xsl:stylesheet>
