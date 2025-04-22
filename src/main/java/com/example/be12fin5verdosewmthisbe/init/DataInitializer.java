package com.example.be12fin5verdosewmthisbe.init;

import com.example.be12fin5verdosewmthisbe.inventory.model.StoreInventory;
import com.example.be12fin5verdosewmthisbe.inventory.repository.StoreInventoryRepository;
import com.example.be12fin5verdosewmthisbe.market_management.market.model.InventoryPurchase;
import com.example.be12fin5verdosewmthisbe.market_management.market.model.InventorySale;
import com.example.be12fin5verdosewmthisbe.market_management.market.repository.InventoryPurchaseRepository;
import com.example.be12fin5verdosewmthisbe.market_management.market.repository.InventorySaleRepository;
import com.example.be12fin5verdosewmthisbe.menu_management.category.model.Category;
import com.example.be12fin5verdosewmthisbe.menu_management.category.repository.CategoryRepository;
import com.example.be12fin5verdosewmthisbe.menu_management.menu.model.Menu;
import com.example.be12fin5verdosewmthisbe.menu_management.menu.model.Recipe;
import com.example.be12fin5verdosewmthisbe.menu_management.menu.repository.MenuRepository;
import com.example.be12fin5verdosewmthisbe.menu_management.menu.repository.RecipeRepository;
import com.example.be12fin5verdosewmthisbe.order.model.Order;
import com.example.be12fin5verdosewmthisbe.order.model.OrderMenu;
import com.example.be12fin5verdosewmthisbe.order.repository.OrderMenuRepository;
import com.example.be12fin5verdosewmthisbe.order.repository.OrderRepository;
import com.example.be12fin5verdosewmthisbe.store.model.Store;
import com.example.be12fin5verdosewmthisbe.store.repository.StoreRepository;
import com.example.be12fin5verdosewmthisbe.user.model.User;
import com.example.be12fin5verdosewmthisbe.user.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final StoreRepository storeRepository;
    private final OrderRepository orderRepository;
    private final PasswordEncoder passwordEncoder;
    private final MenuRepository menuRepository;
    private final CategoryRepository categoryRepository;
    private final OrderMenuRepository orderMenuRepository;
    private final StoreInventoryRepository storeInventoryRepository;
    private final RecipeRepository recipeRepository;
    private final InventorySaleRepository inventorySaleRepository;
    private final InventoryPurchaseRepository inventoryPurchaseRepository;

    public DataInitializer(
            UserRepository userRepository, StoreRepository storeRepository,
            OrderRepository orderRepository, PasswordEncoder passwordEncoder,
            MenuRepository menuRepository, CategoryRepository categoryRepository,
            OrderMenuRepository orderMenuRepository, StoreInventoryRepository storeInventoryRepository,
            RecipeRepository recipeRepository, InventorySaleRepository inventorySaleRepository,
            InventoryPurchaseRepository inventoryPurchaseRepository
    ) {
        this.userRepository = userRepository;
        this.storeRepository = storeRepository;
        this.orderRepository = orderRepository;
        this.passwordEncoder = passwordEncoder;
        this.menuRepository = menuRepository;
        this.categoryRepository = categoryRepository;
        this.orderMenuRepository = orderMenuRepository;
        this.storeInventoryRepository = storeInventoryRepository;
        this.recipeRepository = recipeRepository;
        this.inventorySaleRepository = inventorySaleRepository;
        this.inventoryPurchaseRepository = inventoryPurchaseRepository;

    }

    private List<Order> generateOrdersRandomly(Store store, LocalDate date, int count) {
        Random random = new Random();
        List<Order> orders = new ArrayList<>();
        Order.OrderType[] orderTypes = Order.OrderType.values();

        for (int i = 0; i < count; i++) {
            int hour = random.nextInt(24);         // 0 ~ 23시
            int minute = random.nextInt(60);       // 0 ~ 59분
            int second = random.nextInt(60);       // 0 ~ 59초

            LocalDateTime randomDateTime = LocalDateTime.of(date, LocalTime.of(hour, minute, second));
            Timestamp createdAt = Timestamp.valueOf(randomDateTime);

            orders.add(Order.builder()
                    .tableNumber(random.nextInt(10) + 1)
                    .totalPrice((random.nextInt(5) + 1) * 10000)
                    .status(Order.OrderStatus.PAID)
                    .orderType(orderTypes[random.nextInt(orderTypes.length)])
                    .createdAt(createdAt)
                    .store(store)
                    .build());
        }

        return orders;
    }



    @Override
    public void run(String... args) throws Exception {
        User user1 = User.builder()
                .name("홍길동")
                .email("hong@example.com")
                .password(passwordEncoder.encode("qwer1234!"))
                .businessNumber("123-45-67890")
                .phoneNumber("010-1234-5678")
                .ssn("900101-1234567")
                .build();

        User user2 = User.builder()
                .name("김영희")
                .email("young@example.com")
                .password(passwordEncoder.encode("qwer1234!"))
                .businessNumber("987-65-43210")
                .phoneNumber("010-9876-5432")
                .ssn("920305-2345678")
                .build();

        userRepository.save(user1);
        userRepository.save(user2);

        Store store1 = Store.builder()
                .name("홍길동네 분식")
                .address("서울 강남구 테헤란로 123")
                .phoneNumber("02-123-4567")
                .user(user1) // 연관관계 설정
                .build();

        Store store2 = Store.builder()
                .name("김영희의 김밥천국")
                .address("서울 마포구 독막로 45")
                .phoneNumber("02-987-6543")
                .user(user2) // 연관관계 설정
                .build();

        storeRepository.save(store1);
        storeRepository.save(store2);

        List<Order> apr14Orders = generateOrdersRandomly(store1, LocalDate.of(2025, 4, 14), 30);
        List<Order> apr15Orders = generateOrdersRandomly(store1, LocalDate.of(2025, 4, 15), 30);
        List<Order> apr16Orders = generateOrdersRandomly(store1, LocalDate.of(2025, 4, 16), 30);
        List<Order> apr17Orders = generateOrdersRandomly(store1, LocalDate.of(2025, 4, 17), 30);
        List<Order> apr18Orders = generateOrdersRandomly(store1, LocalDate.of(2025, 4, 18), 30);
        List<Order> apr19Orders = generateOrdersRandomly(store1, LocalDate.of(2025, 4, 19), 30);
        List<Order> apr20Orders = generateOrdersRandomly(store1, LocalDate.of(2025, 4, 20), 30);
        List<Order> apr21Orders = generateOrdersRandomly(store1, LocalDate.of(2025, 4, 21), 30);
        List<Order> may211rders = generateOrdersRandomly(store1, LocalDate.of(2025, 5, 11), 20);

        orderRepository.saveAll(apr14Orders);
        orderRepository.saveAll(apr15Orders);
        orderRepository.saveAll(apr16Orders);
        orderRepository.saveAll(apr17Orders);
        orderRepository.saveAll(apr18Orders);
        orderRepository.saveAll(apr19Orders);
        orderRepository.saveAll(apr20Orders);
        orderRepository.saveAll(apr21Orders);
        orderRepository.saveAll(may211rders);


        Category koreanCategory = categoryRepository.save(
                Category.builder()
                        .name("한식")
                        .store(store1)
                        .build()
        );

// 메뉴 목록 생성
        List<Menu> menuList = List.of(
                Menu.builder().name("김치찌개").price(8000).category(koreanCategory).store(store1).build(),
                Menu.builder().name("된장찌개").price(7500).category(koreanCategory).store(store1).build(),
                Menu.builder().name("비빔밥").price(8500).category(koreanCategory).store(store1).build(),
                Menu.builder().name("제육볶음").price(9000).category(koreanCategory).store(store1).build(),
                Menu.builder().name("불고기").price(10000).category(koreanCategory).store(store1).build(),
                Menu.builder().name("김밥").price(3500).category(koreanCategory).store(store1).build(),
                Menu.builder().name("떡볶이").price(4000).category(koreanCategory).store(store1).build(),
                Menu.builder().name("순대").price(4500).category(koreanCategory).store(store1).build(),
                Menu.builder().name("라면").price(3000).category(koreanCategory).store(store1).build(),
                Menu.builder().name("돈까스").price(9500).category(koreanCategory).store(store1).build()
        );

        menuRepository.saveAll(menuList);

        Random random = new Random();
        List<Menu> allMenus = menuRepository.findAll(); // 메뉴 전체 가져오기
        List<OrderMenu> allOrderMenus = new ArrayList<>();
        List<Order> allOrders = new ArrayList<>();
        allOrders.addAll(apr14Orders);
        allOrders.addAll(apr15Orders);
        allOrders.addAll(apr16Orders);
        allOrders.addAll(apr17Orders);
        allOrders.addAll(apr18Orders);
        allOrders.addAll(apr19Orders);
        allOrders.addAll(apr20Orders);
        allOrders.addAll(apr21Orders);

        for (Order order : allOrders) {
            int menuCount = 1 + random.nextInt(3); // 1~3개 메뉴 선택
            for (int i = 0; i < menuCount; i++) {
                Menu selectedMenu = allMenus.get(random.nextInt(allMenus.size()));
                int quantity = 1 + random.nextInt(3); // 수량 1~3

                OrderMenu orderMenu = OrderMenu.builder()
                        .menu(selectedMenu)
                        .quantity(quantity)
                        .price(selectedMenu.getPrice())
                        .order(order)
                        .build();

                allOrderMenus.add(orderMenu);
            }
        }

        orderMenuRepository.saveAll(allOrderMenus);


        List<StoreInventory> inventories = List.of(
                StoreInventory.builder().name("마늘").expiryDate(10).quantity(new BigDecimal("25.5")).unit("kg").miniquantity(5).store(store1).build(),
                StoreInventory.builder().name("양파").expiryDate(7).quantity(new BigDecimal("18.0")).unit("kg").miniquantity(4).store(store1).build(),
                StoreInventory.builder().name("된장").expiryDate(30).quantity(new BigDecimal("3.5")).unit("kg").miniquantity(1).store(store1).build(),
                StoreInventory.builder().name("고추장").expiryDate(60).quantity(new BigDecimal("2.0")).unit("kg").miniquantity(1).store(store1).build(),
                StoreInventory.builder().name("대파").expiryDate(5).quantity(new BigDecimal("10.0")).unit("kg").miniquantity(3).store(store1).build(),
                StoreInventory.builder().name("참기름").expiryDate(90).quantity(new BigDecimal("1.2")).unit("L").miniquantity(1).store(store1).build()
        );

        storeInventoryRepository.saveAll(inventories);


        List<Recipe> recipes = new ArrayList<>();

// 재고 인덱스를 순회하기 위한 변수
        int inventoryStartIndex = 0;
        int inventorySize = inventories.size();

        for (Menu menu : menuList) {
            // 한 메뉴당 2~3개의 재료 사용
            int ingredientCount = 2 + (menuList.indexOf(menu) % 2); // 2 또는 3개

            for (int i = 0; i < ingredientCount; i++) {
                int inventoryIndex = (inventoryStartIndex + i) % inventorySize;
                StoreInventory inventory = inventories.get(inventoryIndex);

                Recipe recipe = new Recipe();
                recipe.setMenu(menu);
                recipe.setStoreInventory(inventory);
                recipe.setQuantity(BigDecimal.valueOf(1 + i)); // 1kg, 2kg, ...
                recipe.setPrice(BigDecimal.valueOf(1000 + (i * 500))); // 1000원, 1500원 등

                recipes.add(recipe);
            }

            // 다음 메뉴는 재료 시작 인덱스를 하나씩 밀어줌 → 다양하게 섞이도록
            inventoryStartIndex++;
        }

        recipeRepository.saveAll(recipes);

        List<InventorySale> sales = List.of(
                InventorySale.builder().storeInventory(inventories.get(0)).inventoryName("마늘").sellerStoreId(1L)
                        .sellerStoreName("홍길동네 분식").buyerStoreName("마포식당").quantity(new BigDecimal("3.5")).price(3000)
                        .status(InventorySale.saleStatus.available).content("신선한 국산 마늘입니다")
                        .expiryDate(LocalDate.of(2025, 5, 10)).createdAt(Timestamp.valueOf(LocalDateTime.of(2025, 4, 14, 10, 30))).store(store1).build(),

                InventorySale.builder().storeInventory(inventories.get(1)).inventoryName("양파").sellerStoreId(1L)
                        .sellerStoreName("홍길동네 분식").buyerStoreName("종로김밥").quantity(new BigDecimal("5.0")).price(2000)
                        .status(InventorySale.saleStatus.waiting).content("양파 대량 보유 중입니다")
                        .expiryDate(LocalDate.of(2025, 5, 12)).createdAt(Timestamp.valueOf(LocalDateTime.of(2025, 4, 14, 11, 15))).store(store1).build(),

                InventorySale.builder().storeInventory(inventories.get(2)).inventoryName("된장").sellerStoreId(1L)
                        .sellerStoreName("홍길동네 분식").buyerStoreName("테스트매장A").quantity(new BigDecimal("1.5")).price(2500)
                        .status(InventorySale.saleStatus.sold).content("집된장 팝니다")
                        .expiryDate(LocalDate.of(2025, 5, 20)).createdAt(Timestamp.valueOf(LocalDateTime.of(2025, 4, 14, 14, 50))).store(store1).build(),

                InventorySale.builder().storeInventory(inventories.get(3)).inventoryName("고추장").sellerStoreId(1L)
                        .sellerStoreName("홍길동네 분식").buyerStoreName("테스트매장B").quantity(new BigDecimal("2.0")).price(3500)
                        .status(InventorySale.saleStatus.delivery).content("직접 담근 고추장")
                        .expiryDate(LocalDate.of(2025, 5, 25)).createdAt(Timestamp.valueOf(LocalDateTime.of(2025, 4, 15, 9, 10))).store(store1).build(),

                InventorySale.builder().storeInventory(inventories.get(4)).inventoryName("대파").sellerStoreId(1L)
                        .sellerStoreName("홍길동네 분식").buyerStoreName("역삼김밥").quantity(new BigDecimal("4.0")).price(1500)
                        .status(InventorySale.saleStatus.cancelled).content("대파 남아서 팝니다")
                        .expiryDate(LocalDate.of(2025, 5, 18)).createdAt(Timestamp.valueOf(LocalDateTime.of(2025, 4, 15, 13, 25))).store(store1).build(),

                InventorySale.builder().storeInventory(inventories.get(5)).inventoryName("참기름").sellerStoreId(1L)
                        .sellerStoreName("홍길동네 분식").buyerStoreName("성수분식").quantity(new BigDecimal("1.2")).price(5000)
                        .status(InventorySale.saleStatus.available).content("참기름 100% 국산")
                        .expiryDate(LocalDate.of(2025, 6, 1)).createdAt(Timestamp.valueOf(LocalDateTime.of(2025, 4, 16, 15, 40))).store(store1).build(),

                InventorySale.builder().storeInventory(inventories.get(0)).inventoryName("마늘").sellerStoreId(1L)
                        .sellerStoreName("홍길동네 분식").buyerStoreName("이태원매장").quantity(new BigDecimal("2.0")).price(2900)
                        .status(InventorySale.saleStatus.available).content("마늘 소량 판매")
                        .expiryDate(LocalDate.of(2025, 5, 15)).createdAt(Timestamp.valueOf(LocalDateTime.of(2025, 4, 16, 11, 20))).store(store1).build(),

                InventorySale.builder().storeInventory(inventories.get(1)).inventoryName("양파").sellerStoreId(1L)
                        .sellerStoreName("홍길동네 분식").buyerStoreName("테스트매장C").quantity(new BigDecimal("3.0")).price(2100)
                        .status(InventorySale.saleStatus.available).content("양파 중간 크기")
                        .expiryDate(LocalDate.of(2025, 5, 22)).createdAt(Timestamp.valueOf(LocalDateTime.of(2025, 4, 17, 8, 45))).store(store1).build(),

                InventorySale.builder().storeInventory(inventories.get(3)).inventoryName("고추장").sellerStoreId(1L)
                        .sellerStoreName("홍길동네 분식").buyerStoreName("매운맛분식").quantity(new BigDecimal("2.5")).price(3300)
                        .status(InventorySale.saleStatus.waiting).content("고추장 매운맛")
                        .expiryDate(LocalDate.of(2025, 5, 30)).createdAt(Timestamp.valueOf(LocalDateTime.of(2025, 4, 17, 16, 5))).store(store1).build(),

                InventorySale.builder().storeInventory(inventories.get(4)).inventoryName("대파").sellerStoreId(1L)
                        .sellerStoreName("홍길동네 분식").buyerStoreName("연남김밥").quantity(new BigDecimal("1.8")).price(1400)
                        .status(InventorySale.saleStatus.available).content("대파 저렴하게 판매 중")
                        .expiryDate(LocalDate.of(2025, 5, 19)).createdAt(Timestamp.valueOf(LocalDateTime.of(2025, 4, 17, 17, 25))).store(store1).build()
        );

        inventorySaleRepository.saveAll(sales);

        List<InventoryPurchase> purchases = List.of(
                InventoryPurchase.builder().inventoryName("마늘").buyerStoreId(2L).quantity(new BigDecimal("1.0"))
                        .price(3000).status(InventoryPurchase.purchaseStatus.payment).method(InventoryPurchase.purchaseMethod.kakaopay)
                        .createdAt(Timestamp.valueOf(LocalDateTime.of(2025, 4, 18, 10, 15))).inventorySale(sales.get(0)).store(store1).build(),

                InventoryPurchase.builder().inventoryName("마늘").buyerStoreId(3L).quantity(new BigDecimal("0.5"))
                        .price(1500).status(InventoryPurchase.purchaseStatus.delivery).method(InventoryPurchase.purchaseMethod.cash)
                        .createdAt(Timestamp.valueOf(LocalDateTime.of(2025, 4, 18, 12, 0))).inventorySale(sales.get(0)).store(store1).build(),

                InventoryPurchase.builder().inventoryName("양파").buyerStoreId(4L).quantity(new BigDecimal("2.0"))
                        .price(4000).status(InventoryPurchase.purchaseStatus.payment).method(InventoryPurchase.purchaseMethod.credit_card)
                        .createdAt(Timestamp.valueOf(LocalDateTime.of(2025, 4, 18, 14, 30))).inventorySale(sales.get(1)).store(store1).build(),

                InventoryPurchase.builder().inventoryName("된장").buyerStoreId(5L).quantity(new BigDecimal("1.5"))
                        .price(2500).status(InventoryPurchase.purchaseStatus.end).method(InventoryPurchase.purchaseMethod.cash)
                        .createdAt(Timestamp.valueOf(LocalDateTime.of(2025, 4, 18, 16, 0))).inventorySale(sales.get(2)).store(store1).build(),

                InventoryPurchase.builder().inventoryName("고추장").buyerStoreId(6L).quantity(new BigDecimal("2.0"))
                        .price(3500).status(InventoryPurchase.purchaseStatus.waiting).method(InventoryPurchase.purchaseMethod.credit_card)
                        .createdAt(Timestamp.valueOf(LocalDateTime.of(2025, 4, 18, 18, 10))).inventorySale(sales.get(3)).store(store1).build(),

                InventoryPurchase.builder().inventoryName("대파").buyerStoreId(7L).quantity(new BigDecimal("3.0"))
                        .price(4500).status(InventoryPurchase.purchaseStatus.cancelled).method(InventoryPurchase.purchaseMethod.kakaopay)
                        .createdAt(Timestamp.valueOf(LocalDateTime.of(2025, 4, 19, 9, 0))).inventorySale(sales.get(4)).store(store1).build(),

                InventoryPurchase.builder().inventoryName("참기름").buyerStoreId(8L).quantity(new BigDecimal("1.2"))
                        .price(5000).status(InventoryPurchase.purchaseStatus.delivery).method(InventoryPurchase.purchaseMethod.cash)
                        .createdAt(Timestamp.valueOf(LocalDateTime.of(2025, 4, 19, 11, 30))).inventorySale(sales.get(5)).store(store1).build(),

                InventoryPurchase.builder().inventoryName("마늘").buyerStoreId(9L).quantity(new BigDecimal("2.0"))
                        .price(2900).status(InventoryPurchase.purchaseStatus.end).method(InventoryPurchase.purchaseMethod.credit_card)
                        .createdAt(Timestamp.valueOf(LocalDateTime.of(2025, 4, 19, 14, 15))).inventorySale(sales.get(6)).store(store1).build(),

                InventoryPurchase.builder().inventoryName("양파").buyerStoreId(10L).quantity(new BigDecimal("1.0"))
                        .price(2100).status(InventoryPurchase.purchaseStatus.waiting).method(InventoryPurchase.purchaseMethod.kakaopay)
                        .createdAt(Timestamp.valueOf(LocalDateTime.of(2025, 4, 20, 10, 0))).inventorySale(sales.get(7)).store(store1).build(),

                InventoryPurchase.builder().inventoryName("고추장").buyerStoreId(11L).quantity(new BigDecimal("2.5"))
                        .price(3300).status(InventoryPurchase.purchaseStatus.payment).method(InventoryPurchase.purchaseMethod.cash)
                        .createdAt(Timestamp.valueOf(LocalDateTime.of(2025, 4, 20, 12, 45))).inventorySale(sales.get(8)).store(store1).build()
        );

        inventoryPurchaseRepository.saveAll(purchases);

    }
}
