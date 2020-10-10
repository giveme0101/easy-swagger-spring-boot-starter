
swagger:
  # 是否启用swagger
  enable: true
  # 增强型UI是否使用需要账号密码认证
  basic:
    enable: false
    username: organ
    password: 123456
  dockets:
      # 是否启用此docket
    - enable: true
      api:
        group-name: api
        base-package: ${info.basePackage}.webapp.controller.demo
        version: 1.0.0
        title: API接口文档
        description: 描述
      contact:
        name: giveme0101
        url:  https://github.com/giveme0101/easy-swagger-springboot-starter
        email: xiajun94@foxmail.com
      parameters:
        - name: secret
          param-type: header
        - name: tid
          description: trace id   