# 校园二手交易平台

## 项目简介

校园二手交易平台是一个专为大学生设计的二手物品交易系统，旨在为校园师生提供安全、便捷的二手物品交易服务，促进校园资源循环利用。

## 技术栈

### 后端
- **框架**: Spring Boot 2.7.14
- **数据库**: MySQL 8.0
- **ORM**: MyBatis Plus 3.5.3
- **安全**: Spring Security + JWT
- **文档**: Swagger 3.0
- **构建**: Maven

### 前端
- **框架**: Vue.js 3.3.4
- **UI库**: Element Plus 2.3.9
- **状态管理**: Vuex 4.1.0
- **路由**: Vue Router 4.2.4
- **HTTP**: Axios 1.5.0
- **构建**: Vite 4.4.9

## 项目结构

```
campus-trade-platform/
├── backend/                    # 后端项目
│   ├── src/main/java/
│   │   └── com/campus/trade/
│   │       ├── config/         # 配置类
│   │       ├── controller/     # 控制器
│   │       ├── service/        # 业务逻辑
│   │       ├── mapper/         # 数据访问
│   │       ├── entity/         # 实体类
│   │       ├── dto/            # 数据传输对象
│   │       ├── common/         # 公共类
│   │       └── utils/          # 工具类
│   ├── src/main/resources/
│   │   ├── mapper/             # MyBatis映射文件
│   │   └── application.yml     # 配置文件
│   └── pom.xml                 # Maven配置
├── frontend/                   # 前端项目
│   ├── src/
│   │   ├── api/                # API接口
│   │   ├── assets/             # 静态资源
│   │   ├── components/         # 公共组件
│   │   ├── router/             # 路由配置
│   │   ├── store/              # 状态管理
│   │   ├── utils/              # 工具函数
│   │   └── views/              # 页面组件
│   ├── package.json            # 依赖配置
│   └── vite.config.js          # 构建配置
├── database/                   # 数据库脚本
│   └── init.sql                # 初始化脚本
└── docs/                       # 项目文档
```

## 功能特性

### 核心功能
- 🔐 **用户系统**: 注册、登录、个人信息管理
- 📦 **商品管理**: 发布、编辑、搜索、分类管理
- 💰 **交易系统**: 订单创建、状态跟踪、交易确认
- 💬 **消息系统**: 买卖双方实时沟通
- ⭐ **评价系统**: 交易后互相评价
- 📱 **响应式设计**: 支持PC和移动端

### 特色功能
- 🎓 **校园认证**: 基于学号的用户身份验证
- 🏷️ **智能分类**: 多级商品分类管理
- 🔍 **全文搜索**: 支持商品标题和描述搜索
- 📊 **数据统计**: 个人交易数据统计
- 🔔 **消息通知**: 系统消息和交易通知

## 快速开始

### 环境要求
- Java 8+
- Node.js 16+
- MySQL 8.0+
- Maven 3.6+

### 1. 克隆项目
```bash
git clone https://github.com/your-username/campus-trade-platform.git
cd campus-trade-platform
```

### 2. 数据库初始化
```bash
# 创建数据库
mysql -u root -p < database/init.sql
```

### 3. 后端启动
```bash
cd backend
# 修改 application.yml 中的数据库配置
# 启动后端服务
mvn spring-boot:run
```

### 4. 前端启动
```bash
cd frontend
# 安装依赖
npm install
# 启动开发服务器
npm run dev
```

### 5. 访问应用
- 前端地址: http://localhost:3000
- 后端API: http://localhost:8080/api
- API文档: http://localhost:8080/swagger-ui/

## 开发指南

### 后端开发

#### 1. 添加新的API接口
```java
@RestController
@RequestMapping("/api/example")
@Api(tags = "示例接口")
public class ExampleController {
    
    @GetMapping("/list")
    @ApiOperation("获取列表")
    public Result<List<Example>> getList() {
        // 业务逻辑
        return Result.success(data);
    }
}
```

#### 2. 数据库操作
```java
@Service
public class ExampleService {
    
    @Autowired
    private ExampleMapper exampleMapper;
    
    public List<Example> getList() {
        return exampleMapper.selectList(null);
    }
}
```

### 前端开发

#### 1. 添加新页面
```javascript
// 1. 在 views 目录下创建页面组件
// 2. 在 router/index.js 中添加路由
{
  path: '/example',
  name: 'Example',
  component: () => import('@/views/Example.vue'),
  meta: { title: '示例页面' }
}
```

#### 2. API调用
```javascript
// 在 api/index.js 中添加接口
example: {
  getList: () => request.get('/example/list')
}

// 在组件中使用
import api from '@/api'

export default {
  async mounted() {
    const response = await api.example.getList()
    this.list = response.data
  }
}
```

## 部署指南

### 开发环境部署
1. 按照"快速开始"步骤启动项目
2. 前端开发服务器会自动代理API请求到后端

### 生产环境部署
1. **后端部署**:
   ```bash
   cd backend
   mvn clean package
   java -jar target/campus-trade-platform-1.0.0.jar
   ```

2. **前端部署**:
   ```bash
   cd frontend
   npm run build
   # 将 dist 目录部署到 Web 服务器
   ```

3. **数据库配置**:
   - 修改 `application.yml` 中的数据库连接信息
   - 执行 `database/init.sql` 初始化数据库

## 测试账号

| 用户名 | 密码 | 角色 | 说明 |
|--------|------|------|------|
| admin | 123456 | 管理员 | 系统管理员账号 |
| student1 | 123456 | 学生 | 测试学生账号1 |
| student2 | 123456 | 学生 | 测试学生账号2 |

## 开发规范

### 代码规范
- **Java**: 遵循阿里巴巴Java开发手册
- **JavaScript**: 使用ESLint进行代码检查
- **Git提交**: 使用约定式提交格式

### 提交格式
```
feat: 新功能
fix: 修复bug
docs: 文档更新
style: 代码格式调整
refactor: 代码重构
test: 测试相关
```

## 常见问题

### Q: 启动后端时提示数据库连接失败？
A: 请检查 `application.yml` 中的数据库配置，确保MySQL服务已启动且配置正确。

### Q: 前端启动时提示端口被占用？
A: 可以修改 `vite.config.js` 中的端口配置，或者使用 `npm run dev -- --port 3001` 指定端口。

### Q: API请求时提示跨域错误？
A: 前端开发环境已配置代理，生产环境需要在后端配置CORS或使用Nginx代理。

## 贡献指南

1. Fork 本项目
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 创建 Pull Request

## 许可证

本项目采用 MIT 许可证 - 查看 [LICENSE](LICENSE) 文件了解详情。

## 联系方式

- 项目地址: https://github.com/your-username/campus-trade-platform
- 问题反馈: https://github.com/your-username/campus-trade-platform/issues

---

**开发团队** | **版本** v1.0.0 | **更新时间** 2024-11-14
