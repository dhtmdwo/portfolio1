import random
import time
from locust import FastHttpUser, task, between

class OrderUser(FastHttpUser):
    """
    Locust load test for /api/order/create, using realistic store/menu/option combinations.
    """
    wait_time = between(0.5, 1.5)
    # Class-level cache to prevent per-user reload
    store_menu_map = {}
    loaded = False

    order_types = ["hall", "coupang", "baemin", "yogiyo"]

    def fetch_store_menus(self, store_id, retries=3):
        """Helper: fetch menus for a store with simple retry/backoff."""
        for attempt in range(retries):
            try:
                resp = self.client.get(f"/api/store/{store_id}/menus")
                if resp.status_code == 200:
                    return resp.json()
            except Exception:
                # last attempt will naturally return []
                time.sleep(1)
        return []

    def on_start(self):
        # Load store->menu map once per Locust process
        if not OrderUser.loaded:
            for store_id in range(1, 101):
                OrderUser.store_menu_map[store_id] = self.fetch_store_menus(store_id)
                # Throttle initial requests to avoid socket reset
                time.sleep(0.1)
            OrderUser.loaded = True
        # Instance-level reference
        self.store_menu_map = OrderUser.store_menu_map

    @task(1)
    def create_order(self):
        # 1) 랜덤 store, table, orderType
        store_id = random.randint(1, 100)
        table_number = random.randint(1, 9)
        order_type = random.choice(self.order_types)

        # 2) 해당 store 메뉴 목록 활용
        menus = self.store_menu_map.get(store_id, [])
        if not menus:
            return

        # 3) 1~3개의 메뉴 항목 랜덤 선택
        order_menus = []
        for _ in range(random.randint(1, 3)):
            candidate = random.choice(menus)
            opts = candidate.get("optionIds", [])
            order_menus.append({
                "menuId": candidate["menuId"],
                "quantity": random.randint(1, 5),
                "price": candidate.get("price", 0),
                # 옵션도 랜덤 샘플
                "optionIds": random.sample(opts, k=random.randint(0, len(opts)))
            })

        payload = {
            "storeId": str(store_id),
            "tableNumber": str(table_number),
            "orderType": order_type,
            "orderMenus": order_menus
        }

        # 4) 주문 생성 API 호출
        self.client.post("/api/order/create", json=payload)
