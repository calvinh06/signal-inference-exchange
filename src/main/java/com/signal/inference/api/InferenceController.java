package com.signal.inference.api;
import com.signal.inference.contract.*;
import com.signal.inference.engine.ExchangeService;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.*;
@RestController @RequestMapping("/api/v1/inference") public class InferenceController {
 private final ExchangeService exchange;
 public InferenceController(ExchangeService exchange){this.exchange=exchange;}
 // DEMO ONLY: in production derive tenant from validated identity, never a caller-controlled header.
 @PostMapping public InferenceResult infer(@RequestHeader("X-Demo-Tenant") String tenant,@Valid @RequestBody InferenceRequest request){return exchange.execute(tenant,request);}
 @GetMapping("/providers") public List<ProviderDescriptor> providers(){return exchange.catalog();}
 @ExceptionHandler(NoSuchElementException.class) ResponseEntity<Map<String,String>> notFound(Exception ex){return ResponseEntity.status(422).body(Map.of("error",ex.getMessage()));}
 @ExceptionHandler(IllegalArgumentException.class) ResponseEntity<Map<String,String>> invalid(Exception ex){return ResponseEntity.badRequest().body(Map.of("error",ex.getMessage()));}
}
