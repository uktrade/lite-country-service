package uk.gov.bis.lite.countryservice.config;


import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.db.DataSourceFactory;
import org.apache.commons.lang3.StringUtils;

public class SchemaAwareDataSourceFactory extends DataSourceFactory {

  private String schema;

  @JsonProperty
  public String getSchema() {
    return schema;
  }

  @JsonProperty
  public void setSchema(String schema) {
    this.schema = schema;
    setUrl(fixUrl(getUrl()));
  }

  @Override
  public void setUrl(String url) {
    super.setUrl(fixUrl(url));
  }

  private String fixUrl(String url) {
    if (!url.contains("currentSchema=") && StringUtils.isNoneBlank(schema)) {
      if (!url.contains("?")) {
        url += "?";
      } else {
        url += "&";
      }
      url += "currentSchema=" + schema;
    }
    return url;
  }
}
