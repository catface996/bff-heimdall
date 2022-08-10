package com.catface.heimdall.app.common.model;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties.ZuulRoute;
import org.springframework.util.CollectionUtils;

public class DiffRouteModel {

  private List<PathNode> pathNodes = new ArrayList<>();

  private String url;

  private String serviceId;


  class PathNode {

    private String id;
    private String path;

    public String getId() {
      return id;
    }

    public void setId(String id) {
      this.id = id;
    }

    public String getPath() {
      return path;
    }

    public void setPath(String path) {
      this.path = path;
    }
  }

  public Map<String, ZuulRoute> convert() {

    Map<String, ZuulRoute> ruoteMap = new LinkedHashMap<>();
    if (!CollectionUtils.isEmpty(this.pathNodes)) {
      for (PathNode pathNode : pathNodes) {
        ZuulRoute zuulRoute = new ZuulRoute();
        zuulRoute.setId(pathNode.getId());
        zuulRoute.setPath(pathNode.getPath());
        zuulRoute.setUrl(this.url);
        zuulRoute.setServiceId(this.serviceId);
      }
    }
    return ruoteMap;
  }

  public List<PathNode> getPathNodes() {
    return pathNodes;
  }

  public void setPathNodes(
      List<PathNode> pathNodes) {
    this.pathNodes = pathNodes;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public String getServiceId() {
    return serviceId;
  }

  public void setServiceId(String serviceId) {
    this.serviceId = serviceId;
  }
}


