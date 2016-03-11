# riemann-plugin-amqp

AMQP event source for Riemann.

## Usage

```
(load-plugins)
(amqp/source {:host "localhost" :vhost "/" :username "guest" :password "guest" :exchange "riemann" :routing-key "#"})
```

Messages routed to the queue will be parsed as JSON matching the Riemann event structure and injected into the core as normal.

## License

Copyright © 2016 Valeri Sokolov

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
