<?xml version="1.0" ?>
<XmlRootElement>
<%@ page import="java.io.*,java.util.*,java.sql.*,java.net.URLDecoder"
%><%@ page import="javax.servlet.http.*,javax.servlet.*" 
%><%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"
%><%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql"
%><%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"
%><portlet:defineObjects 
/><%@ page language="java" contentType="text/xml; charset=UTF-8"
    pageEncoding="UTF-8"
%><%@ page import="javax.portlet.PortletPreferences"
%><%  File f = new File("/opt/avaa/liferay-portal/tomcat/shared/radar.properties");
      Properties prop = new Properties();
      FileInputStream in = new FileInputStream(f);
      prop.load(in);
      String salasana = prop.getProperty("salasana");
%><c:set var="salasana"><%= salasana %></c:set><c:set
 var="startdate"><%= request.getParameter("startdate") %></c:set><c:set
 var="enddate"><%= request.getParameter("enddate") %></c:set><c:set
 var="alue"><%= URLDecoder.decode(request.getParameter("alue"), "UTF-8") %></c:set><c:set
 var="lyh"><%= null == request.getParameter("tutka") ? "van" : request.getParameter("tutka") %></c:set><c:set
 var="kulma"><%= null == request.getParameter("kulma") ? "0_7" : request.getParameter("kulma") %></c:set><c:set
 var="vertices"><%= null == request.getParameter("vertices") ? 10 : Integer.parseInt(request.getParameter("vertices")) %></c:set><c:set
 var="taulu"><%= null == request.getParameter("tyyppi") ? "z" : request.getParameter("tyyppi") %></c:set>
<sql:setDataSource var="radarmetadata" driver="org.postgresql.Driver"
     url="jdbc:postgresql://db4.csc.fi:5510/radarmetadata"
     user="radar-read"  password="${salasana}"
/><sql:query dataSource="${radarmetadata}" var="result">
SELECT data_time, ST_AsText(polygons) FROM ${taulu}_${lyh}_${kulma} WHERE data_time > to_timestamp(?, 'YYYY-MM-DD') AND data_time < to_timestamp(?, 'YYYY-MM-DD') AND vertices > int4(?) AND ST_Contains(ST_Polygon(ST_GeomFromText('LINESTRING(${alue})'),2393),polygons);
<sql:param value="${startdate}" />
<sql:param value="${enddate}" />
<sql:param value="${vertices}" />
</sql:query>
<c:forEach var="row" items="${result.rows}">
	<datetime>
    <c:out value="${row.data_time}"/></datetime><polygon>
	<c:out value="${row.st_astext}"/></polygon>
</c:forEach>
</XmlRootElement>