const categories = [
  { id: 1, name: '数码产品' },
  { id: 2, name: '学习用品' },
  { id: 3, name: '生活用品' },
  { id: 4, name: '服装鞋帽' },
  { id: 5, name: '运动器材' },
  { id: 6, name: '其他' }
]

const titlesByCategory = {
  1: ['iPhone 13', 'iPad Pro', 'MacBook Air', '小米手环', '蓝牙耳机', '机械键盘', '显卡 RTX 3060'],
  2: ['高等数学教材', '英语四级资料', '中性笔一盒', '笔记本若干', '草稿纸', '计算器'],
  3: ['台灯', '电水壶', '收纳盒', '落地风扇', '雨伞', '床上书桌'],
  4: ['羽绒服', '运动鞋', '卫衣', '牛仔裤', '棒球帽'],
  5: ['篮球', '羽毛球拍', '瑜伽垫', '滑板', '骑行头盔'],
  6: ['乐器尤克里里', '拼图', '相框', '绿植']
}

function randomFrom(arr) {
  return arr[Math.floor(Math.random() * arr.length)]
}

function randomPrice(base = 10, max = 9999) {
  const n = Math.max(base, Math.floor(Math.random() * max))
  return Math.round(n / 5) * 5
}

function imageUrl(seed, w = 600, h = 400) {
  return `https://picsum.photos/seed/${seed}/${w}/${h}`
}

function genProduct(id, categoryId) {
  const title = randomFrom(titlesByCategory[categoryId] || ['二手好物'])
  const price = randomPrice(20, 8000)
  const originalPrice = price + randomPrice(50, 2000)
  return {
    id,
    userId: Math.floor(Math.random() * 3) + 1,
    categoryId,
    title: `${title} ${id}`,
    price,
    originalPrice,
    conditionLevel: Math.ceil(Math.random() * 5),
    location: randomFrom(['宿舍楼下', '图书馆', '食堂', '操场', '教学楼']),
    viewCount: Math.floor(Math.random() * 500),
    favoriteCount: Math.floor(Math.random() * 80),
    status: 1,
    images: JSON.stringify([imageUrl(`${categoryId}-${id}`, 800, 600)]),
    createdAt: new Date(Date.now() - Math.floor(Math.random() * 10) * 86400000).toISOString()
  }
}

let allProductsCache = null

function buildAllProducts() {
  if (allProductsCache) return allProductsCache
  const products = []
  let id = 1
  for (const c of categories) {
    const count = 18
    for (let i = 0; i < count; i++) {
      products.push(genProduct(id++, c.id))
    }
  }
  allProductsCache = products
  return products
}

export function getCategories() {
  return categories.slice()
}

export function getFeaturedProducts(limit = 12) {
  const list = buildAllProducts()
  return list.slice(0, limit)
}

export function queryProducts(params = {}) {
  const { keyword = '', categoryId = null, page = 1, size = 20 } = params
  const list = buildAllProducts()
  let filtered = list
  if (categoryId) {
    filtered = filtered.filter(p => p.categoryId === Number(categoryId))
  }
  if (keyword) {
    const k = String(keyword).toLowerCase()
    filtered = filtered.filter(p => p.title.toLowerCase().includes(k))
  }
  const total = filtered.length
  const start = (page - 1) * size
  const end = start + size
  const records = filtered.slice(start, end)
  return { records, total, current: page, size }
}

export function getProductById(id) {
  const list = buildAllProducts()
  return list.find(p => p.id === Number(id)) || null
}
