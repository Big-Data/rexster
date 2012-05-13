package com.tinkerpop.rexster.config;

import com.tinkerpop.blueprints.Graph;
import org.apache.commons.configuration.Configuration;

public interface GraphConfiguration {
    Graph configureGraphInstance(final Configuration properties) throws GraphConfigurationException;
}
