package com.huihuang.feign.ribbon;

import com.huihuang.feign.properties.ReinforceOptions;
import com.netflix.client.config.CommonClientConfigKey;
import com.netflix.client.config.DefaultClientConfigImpl;
import feign.Request;

public class ReinforceFeignOptionsClientConfig extends DefaultClientConfigImpl {

		public ReinforceFeignOptionsClientConfig(Request.Options options) {
		    if (options == null){
		        throw new NullPointerException("The options parameter cannot be Null!");
            }
			setProperty(CommonClientConfigKey.ConnectTimeout, options.connectTimeoutMillis());
			setProperty(CommonClientConfigKey.ReadTimeout, options.readTimeoutMillis());

			if (options instanceof ReinforceOptions.Options && ((ReinforceOptions.Options) options).isAllowedRetry()){
                setProperty(CommonClientConfigKey.MaxAutoRetries, ((ReinforceOptions.Options) options).getMaxAutoRetries());
                setProperty(CommonClientConfigKey.MaxAutoRetriesNextServer, ((ReinforceOptions.Options) options).getMaxAutoRetriesNextServer());
                setProperty(CommonClientConfigKey.OkToRetryOnAllOperations, true);
            }
		}

		@Override
		public void loadProperties(String clientName) {

		}

		@Override
		public void loadDefaultValues() {

		}

	}
