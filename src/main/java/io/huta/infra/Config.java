package io.huta.infra;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public record Config(ServerCfg serverCfg, PrometheusCfg prometheusCfg) {

    public static Config load(String path) throws IOException {
        Properties p = new Properties();
        try (InputStream systemResourceAsStream = ClassLoader.getSystemResourceAsStream(path)) {
            p.load(systemResourceAsStream);
        }
        int serverPort = stringAsInt(p.getProperty("server.port"));
        int prometheusPort = stringAsInt(p.getProperty("prometheus.port"));

        return new Config(new ServerCfg(serverPort), new PrometheusCfg(prometheusPort));
    }

    static int stringAsInt(String props) {
        checkArgument(isNotBlank(props));
        return asInt(props);
    }

    public static void checkArgument(boolean expression) {
        if (!expression) {
            throw new IllegalArgumentException();
        }
    }

    static boolean isNotBlank(String str) {
        return !isBlank(str);
    }

    static boolean isBlank(String str) {
        return str == null || str.trim().isEmpty();
    }

    static int asInt(String props) {
        return Integer.parseInt(props);
    }

    public record ServerCfg(int port) {
        public ServerCfg {
            checkArgument(port > 0);
        }
    };

    public record PrometheusCfg(int port) {
        public PrometheusCfg {
            checkArgument(port > 0);
        }
    };
}
