package com.tang.cloud.Controller;


import com.tang.cloud.Service.PaymentService;
import com.tang.cloud.entities.CommonResult;
import com.tang.cloud.entities.Payment;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.TimeUnit;

@RestController
@Slf4j
public class PaymentController {

    @Resource
    private PaymentService paymentService;

    @Value("${server.port}")
    private String serverPort;

    @Resource
    private DiscoveryClient discoveryClient;

    @PostMapping(value = "/payment/create")
    public CommonResult create(@RequestBody Payment payment){
        int result = paymentService.create(payment);
        log.info("----插入结果"+result);
        if(result>0)
            return new CommonResult(200,"插入成功,服务器端口号："+serverPort,payment);
        else
            return new CommonResult(444,"插入失败",null);
    }

    @GetMapping(value = "/payment/get/{id}")
    public CommonResult getPaymentById(@PathVariable("id") Long id){
        Payment payment = paymentService.getPaymentById(id);
        log.info("----插入结果"+ payment.toString() +"哈啊啊啊啊");
        if(payment != null)
            return new CommonResult(200,"查询成功,port :"+ serverPort,payment);
        else
            return new CommonResult(444,"无记录",null);
    }

    @GetMapping("/payment/discovery")
    public Object discovery(){
//        List<String> services = discoveryClient.getServices();
//        for (String elemtent : services) {
//            log.info("**********element" + elemtent);
//        }
        List<ServiceInstance> instances = discoveryClient.getInstances("CLOUD-PAYMENT-SERVICE");
        for (ServiceInstance instance : instances) {
            log.info(instance.getServiceId()+"\t"+instance.getHost()+"\t"+instance.getPort()+"\t"+instance.getUri());
        }

        return this.discoveryClient;
    }

    @GetMapping(value = "/payment/feign/timeout")
    public String paymentFeignTimeout()
    {
        // 业务逻辑处理正确，但是需要耗费3秒钟
        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return serverPort;
    }

}
