package unicafe

import (
	"context"
	"encoding/json"
	"fmt"
	"io"
	"net/http"
	"strings"
	"time"
)

const (
	// APIURL is the UniCafe WordPress API base.
	APIURL = "https://unicafe.fi/wp-json/swiss/v1"
	// RestaurantsEndpoint lists all restaurants and their menus.
	RestaurantsEndpoint = "/restaurants/?lang=fi"
)

// Client fetches the UniCafe restaurants feed.
type Client struct {
	// BaseURL overrides APIURL, mainly for tests.
	BaseURL string
	// HTTPClient performs the requests; defaults to a 30s client.
	HTTPClient *http.Client
}

// FetchRestaurantFeed returns the raw restaurants feed.
func (c *Client) FetchRestaurantFeed(ctx context.Context) ([]FeedItem, error) {
	base := c.BaseURL
	if base == "" {
		base = APIURL
	}
	url := strings.TrimSuffix(base, "/") + RestaurantsEndpoint

	httpClient := c.HTTPClient
	if httpClient == nil {
		httpClient = &http.Client{Timeout: 30 * time.Second}
	}

	req, err := http.NewRequestWithContext(ctx, http.MethodGet, url, nil)
	if err != nil {
		return nil, err
	}

	resp, err := httpClient.Do(req)
	if err != nil {
		return nil, fmt.Errorf("unicafe: fetching %s: %w", url, err)
	}
	defer resp.Body.Close()

	if resp.StatusCode != http.StatusOK {
		return nil, fmt.Errorf("unicafe: unexpected status %d from %s", resp.StatusCode, url)
	}

	body, err := io.ReadAll(io.LimitReader(resp.Body, 32<<20))
	if err != nil {
		return nil, fmt.Errorf("unicafe: reading response: %w", err)
	}

	var items []FeedItem
	if err := json.Unmarshal(body, &items); err != nil {
		return nil, fmt.Errorf("unicafe: decoding response: %w", err)
	}
	return items, nil
}
