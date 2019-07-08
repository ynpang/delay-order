# RabbitMQ实现延时消息

#项目使用框架介绍

RabbitMQ

RabbitMQ是一个被广泛使用的开源消息队列。它是轻量级且易于部署的，它能支持多种消息协议。RabbitMQ可以部署在分布式和联合配置中，以满足高规模、高可用性的需求。

Exchange类型

direct:消息中的路由键(routing key) 如果和Binding中的binding key一致，交换器就将消息发到对应的队列中。完全匹配、单播的模式

fanout:每个发到fanout类型交换器的消息都会分到所有绑定的队列上去

topic: topic交换器通过模式匹配分配消息

headers: headers和direct交换器完全一致，但性能差很多，目前已经用不到了
#RabbitMQ的消息模型

#业务场景说明

用于解决用户下单以后，订单超时如何取消订单的问题。
用户进行下单操作（会有锁定商品库存、使用优惠券、积分一系列的操作）；
生成订单，获取订单的id；
获取到设置的订单超时时间（假设设置的为60分钟不支付取消订单）；
按订单超时时间发送一个延迟消息给RabbitMQ，让它在订单超时后触发取消订单的操作；
如果用户没有支付，进行取消订单操作（释放锁定商品库存、返还优惠券、返回积分一系列操作）。

交换机及队列说明

mall.order.direct（取消订单消息队列所绑定的交换机）:绑定的队列为mall.order.cancel，一旦有消息以mall.order.cancel为路由键发过来，会发送到此队列。
mall.order.direct.ttl（订单延迟消息队列所绑定的交换机）:绑定的队列为mall.order.cancel.ttl，一旦有消息以mall.order.cancel.ttl为路由键发送过来，会转发到此队列，并在此队列保存一定时间，等到超时后会自动将消息发送到mall.order.cancel（取消订单消息消费队列）。
添加延迟消息的发送者CancelOrderSender

用于向订单延迟消息队列（mall.order.cancel.ttl）里发送消息。