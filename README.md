# study_batch
1. @StepScope  
2. @JobScope

> 외부 내부에서 파라미터(Job Parameter)를 받아 여러 BATCH 컴포넌트에서 사용할 수 있게 지원함  

## Job Parameter
> 사용하기 위해서는 spring batch 전용 scope 인 @StepScope 와 @JobScope 를 선언해야함.
> 사용가능 타입 : Double, Long, Date, String  
> null 선언이 가능함 ? 어플리케이션 실행 시점에 할당하지 않기 때문
> @Value 를 통해서 사용 가능
> Job Parameters 를 사용하기 위해선 꼭 @StepScope, @JobScope로 Bean을 생성해야함!!

### 사용법
```
@Value("#{jobParameter[파라미터명]}")
```

## @JobScope
> Step 선언문에서 사용 가능  

## @StepScope
> tasklet, itemReader, itemWriter, itemProcessor 에서 사용 가능  

"Spring Bean 의 기본 Scope는 sigleton"  
그러나 @StepScope를 사용하게 되면 Spring Batch가 Spring 컨테이너를 통해 지정된  
Step의 실행 시점에 해당 컴포넌트를 Spring Bean으로 생성  
@JobScope는 Job 실행시점에 Bean이 생성  
  
즉, Bean 의 생성 시점을 지정된 Scope 가 실행되는 시점으로 지연  

### 위 장점은? 
1. JobParameter 의 Late Binding 이 가능해짐  
> Application이 실행되는 시점이 아니더라도 Controller나 Service와 같은 비지니스 로직 처리 단계에서 Job Parameter를 할당시킬 수 있습니다.  

2. 동일한 컴포넌트를 병렬 혹은 동시에 사용할때 유용
> @StepScope 없이 Step을 병렬로 실행시키게 되면 서로 다른 Step에서   
> 하나의 Tasklet을 두고 마구잡이로 상태를 변경하려고 할것입니다.  
> 하지만 @StepScope가 있다면 각각의 Step에서 별도의 Tasklet을 생성하고  
> 관리하기 때문에 서로의 상태를 침범할 일이 없습니다.  


## 왜 Job Parameter를 써야하나?
1. 시스템 변수를 사용하면 되지않나? -> job parameter 관련 기능을 못씀
spring batch는 같은 jobparameter로  
같은 job 을 두번 실행하지 않는데 시스템 변수를 사용하면 이 기능이 작동 안함  
-> 같은 파라미터로 job을 두번 실행이 될 여지가 있음. 또한 Parameter 관련 메타테이블이 관리가 되지 않음.  

  
2. command line이 아닌 다른 방법으로 실행이 어려움.
> 그외 동적으로 작동하기 위한 시스템 변수를 변경하기위한 환경설정을 추가적으로 작성해야함.




